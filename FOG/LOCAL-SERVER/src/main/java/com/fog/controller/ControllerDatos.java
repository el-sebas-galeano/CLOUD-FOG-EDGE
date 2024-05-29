package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;


public class ControllerDatos implements Runnable {

    @Override
    public void run() {
        try(ZContext context = new ZContext()){
            Socket socketProxy = context.createSocket(SocketType.PULL);
            socketProxy.bind("tcp://localhost:5220");

            while (true) {
                System.out.println("Esperando.......");
                String data = socketProxy.recvStr();
                System.out.println(data);
            }
        }
    }
    
}
