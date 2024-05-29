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
            socketPull.bind("tcp://localhost:5557");
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Esperando.........");
                byte[] mensajeBytes = socketPull.recv(0);
                String mensaje = new String(mensajeBytes, ZMQ.CHARSET);       
                System.out.println(mensaje);
            }
            socketPull.close();
        }
    }
}
