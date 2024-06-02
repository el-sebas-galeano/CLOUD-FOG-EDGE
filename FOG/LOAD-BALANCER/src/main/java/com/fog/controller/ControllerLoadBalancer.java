package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class ControllerLoadBalancer implements Runnable{
    private String urlPull= Direcciones.DIRECCION_IP_LOAD_BALANCER;
    private String urlPush= Direcciones.DIRECCION_IP_PROXYA;
    private String puertoPull = Direcciones.PUERTO_LOAD_BALANCER_PULL ;
    private String puertoPush = Direcciones.PUERTO_PROXY_BALANCER_PUSH;
    private ZContext context;
    private Socket socketPull;
    private Socket socketPush;
    public ControllerLoadBalancer() {
        this.context = new ZContext();
        socketPull = crearPullSocket("tcp://"+urlPull+":"+ puertoPull);
        socketPush = crearPushSocket("tcp://"+urlPush+":"+puertoPush);
    }

    @Override
    public void run() {
        startfailureListener();
        while (true) {
            System.out.println("Esperando.......");
            String data = socketPull.recvStr();
            System.out.println("Received data: " + data);
            socketPush.send(data);
            System.out.println("Sending data: " + data);
        }
    }
    public Socket crearPushSocket(String url){
        Socket socket = context.createSocket(SocketType.PUSH);
        socket.connect(url);
        return socket;
    }
    public Socket crearPullSocket(String url){
        Socket socket = context.createSocket(SocketType.PULL);
        socket.bind(url);
        return socket;
    }

    private void startfailureListener(){
        new Thread(()-> {
            try(Socket socketNotificacion = context.createSocket(SocketType.REP)){
                socketNotificacion.bind("tcp://*:"+Direcciones.PUERTO_MONITOR_PROXY_NOTIFICACION);
                while (true) {
                    String mensaje= socketNotificacion.recvStr();
                    if(mensaje.startsWith("failure")){
                        String[] parts= mensaje.split(" ");
                        String url= parts[1];
                        actualizarDireccion(url);
                        socketNotificacion.send("url Actualizada!"+ url);
                    }
                }
            }
        }).start();;
    }
    private synchronized void actualizarDireccion(String url){
        System.out.println("Actualizando dirección a: " + url);
        socketPush.close();
        socketPush= crearPushSocket(url);
    }
}
