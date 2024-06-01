package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class ControllerLoadBalancer implements Runnable{
    private String urlPull= "localhost";
    private String urlPush= "localhost";
    private String puertoPull =Integer.toString(5120) ;
    private String puertoPush = Integer.toString(5200);
    private ZContext context;
    private Socket socketPull;
    private Socket socketPush;
    private Socket socketNotificacion;
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
                socketNotificacion.bind("tcp://localhost:5410");
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