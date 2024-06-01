package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class ControllerMonitor implements Runnable {

    
    @Override
    public void run() {
        try(ZContext context = new ZContext()){
            Socket socketMonitor = context.createSocket(SocketType.REP);
            socketMonitor.bind("tcp://*:"+Direcciones.PUERTO_MONITOR_PROXY_BEAT); 
            while(!Thread.currentThread().isInterrupted()){
                String message = socketMonitor.recvStr();
                if(message!=null&& message.startsWith("Ok")){
                    socketMonitor.send(message);
                }
            }
        }
    }  
}
