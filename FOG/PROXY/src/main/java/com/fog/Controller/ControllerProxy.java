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
            loadBalancerSocket.bind(url_lb);

            Socket localServerSocket = context.createSocket(SocketType.PUSH);
            localServerSocket.connect(url_ls);

            while (true) {
                System.out.println("Esperando......");
                String data = loadBalancerSocket.recvStr();
                if(data.startsWith("M")){
                    localServerSocket.send(data);
                }
                System.out.println(data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
