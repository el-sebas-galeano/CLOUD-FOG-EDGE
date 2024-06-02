package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class ControllerMonitor implements Runnable {

    
    @Override
    public void run() {
        try(ZContext context = new ZContext()){
            Socket socketMonitor = context.createSocket(SocketType.REP);
            socketMonitor.bind("tcp://*:5400"); 
            while(!Thread.currentThread().isInterrupted()){
                String message = socketMonitor.recvStr();
                if(message!=null&& message.startsWith("Ok")){
                    socketMonitor.send(message);
                }
            }
        }
    }  
}
