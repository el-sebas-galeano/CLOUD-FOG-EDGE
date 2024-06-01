package com.edge;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class App 
{
    public static void main( String[] args )
    {
        try(ZContext context = new ZContext()){
            Socket socketPull = context.createSocket(SocketType.PULL);
            socketPull.bind("tcp://*:"+Direcciones.PUERTO_ACTIVADOR_SENSOR);
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Esperando sensor de humo!...");
                String message = socketPull.recvStr();
                if (message != null) {
                    System.out.println("Received: " + message);
                }                    
            }
            socketPull.close();
        }
    }
}
