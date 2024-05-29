package com.edge;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class App 
{
    public static void main( String[] args )
    {
        try(ZContext context = new ZContext()){
            Socket socketCalidad=context.createSocket(SocketType.REP);
            Socket socketProxy = context.createSocket(SocketType.PUSH);
            socketCalidad.bind("tcp://localhost:5110");
            socketProxy.connect("tcp://localhost:5120");
            System.out.println("Esperando Alarmas!...");
            while (!Thread.currentThread().isInterrupted()) {
                String alarma = socketCalidad.recvStr();
                System.out.println(alarma);
                socketProxy.send(alarma);
                socketCalidad.send("Alarma registrada con exito!");
            }
        }
    }
}
