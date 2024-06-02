package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class ControllerLoadBalancer implements Runnable {
    private String urlPull = Direcciones.DIRECCION_IP_LOAD_BALANCER;
    private String urlPush = Direcciones.DIRECCION_IP_PROXYA;
    private String puertoPull = Direcciones.PUERTO_LOAD_BALANCER_PULL;
    private String puertoPush = Direcciones.PUERTO_PROXY_BALANCER_PUSH;
    private ZContext context;
    private Socket socketPull;
    private Socket socketPush;
    private final Object lock = new Object();

    public ControllerLoadBalancer() {
        this.context = new ZContext();
        socketPull = crearPullSocket("tcp://" + urlPull + ":" + puertoPull);
        socketPush = crearPushSocket("tcp://" + urlPush + ":" + puertoPush);
    }

    @Override
    public void run() {
        startFailureListener();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Esperando.......");
                String data = socketPull.recvStr();
                System.out.println("Received data: " + data);

                synchronized (lock) {
                    socketPush.send(data);
                }

                System.out.println("Sending data: " + data);
            }
        } catch (Exception e) {
            System.err.println("Exception in run method: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeSockets();
        }
    }

    public Socket crearPushSocket(String url) {
        Socket socket = context.createSocket(SocketType.PUSH);
        try {
            socket.connect(url);
        } catch (Exception e) {
            System.err.println("Error creating PUSH socket: " + e.getMessage());
            e.printStackTrace();
        }
        return socket;
    }

    public Socket crearPullSocket(String url) {
        Socket socket = context.createSocket(SocketType.PULL);
        try {
            socket.bind(url);
        } catch (Exception e) {
            System.err.println("Error creating PULL socket: " + e.getMessage());
            e.printStackTrace();
        }
        return socket;
    }

    private void startFailureListener() {
        new Thread(() -> {
            try (Socket socketNotificacion = context.createSocket(SocketType.REP)) {
                socketNotificacion.bind("tcp://*:" + Direcciones.PUERTO_MONITOR_PROXY_NOTIFICACION);
                while (!Thread.currentThread().isInterrupted()) {
                    String mensaje = socketNotificacion.recvStr();
                    if (mensaje.startsWith("failure")) {
                        String[] parts = mensaje.split(" ");
                        String url = parts[1];
                        actualizarDireccion(url);
                        socketNotificacion.send("URL actualizada: " + url);
                    }
                }
            } catch (Exception e) {
                System.err.println("Exception in failure listener: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void actualizarDireccion(String url) {
        System.out.println("Actualizando dirección a: " + url);

        synchronized (lock) {
            try {
                socketPush.close();
                socketPush = crearPushSocket(url);
            } catch (Exception e) {
                System.err.println("Error updating address: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void closeSockets() {
        if (socketPull != null) {
            socketPull.close();
        }
        if (socketPush != null) {
            socketPush.close();
        }
        if (context != null) {
            context.close();
        }
    }
}
