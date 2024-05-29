package com.fog.Controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class ControllerProxy implements Runnable{

    public String url="";

    public ControllerProxy(String url){
        this.url=url;
    }

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            Socket loadBalancerSocket = context.createSocket(SocketType.PULL);
            loadBalancerSocket.bind(url);
            while (true) {
                System.out.println("Esperando......");
                String data = loadBalancerSocket.recvStr();
                System.out.println(data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
