package com.fog.controller;


import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

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
            socketCloud.connect("tcp://localhost:5230");

            Socket localServerSocket = context.createSocket(SocketType.PUSH);
            localServerSocket.connect(url_ls);

            while (true) {
                System.out.println("Esperando......");
                String data = loadBalancerSocket.recvStr();
                socketCloud.send(data);
                String reply = socketCloud.recvStr();
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
