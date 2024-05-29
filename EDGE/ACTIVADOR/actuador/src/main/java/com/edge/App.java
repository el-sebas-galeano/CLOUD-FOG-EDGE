package com.edge;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class App 
{
    public static void main( String[] args )
    {
        try(ZContext context = new ZContext()){
            Socket socketPull = context.createSocket(SocketType.PULL);
            socketPull.bind("tcp://localhost:5100");
            while (!Thread.currentThread().isInterrupted()) {
                String message = socketPull.recvStr();
                if (message != null) {
                    System.out.println("Received: " + message);
                }                    
            }
            socketPull.close();
        }
    }
}
