package com.fog.controller;


import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class ControllerProxy implements Runnable{

    public String url_lb = "";
    public String url_ls = ""; 

    public ControllerProxy(String url_lb, String url_ls){
        this.url_lb=url_lb;
        this.url_ls = url_ls;
    }

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            Socket loadBalancerSocket = context.createSocket(SocketType.PULL);
            Socket socketCloud = context.createSocket(SocketType.REQ);
            System.out.println(url_lb);
            loadBalancerSocket.bind(url_lb);
            socketCloud.connect("tcp://"+Direcciones.DIRECCION_IP_CLOUD+":"+Direcciones.PUERTO_PROXY_CLOUD);

            Socket localServerSocket = context.createSocket(SocketType.PUSH);
            localServerSocket.connect(url_ls);

            while (true) {
                System.out.println("Esperando......");
                String data = loadBalancerSocket.recvStr();
                long startTime= System.nanoTime();
                socketCloud.send(data);
                String reply = socketCloud.recvStr();
                long endTime= System.nanoTime();
                System.out.println("Tiempo de respuesta: "+(endTime-startTime)+" nanosegundos");
                System.out.println(reply);
                if(data.startsWith("M")){
                    localServerSocket.send(data.getBytes());
                }
                System.out.println(data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
