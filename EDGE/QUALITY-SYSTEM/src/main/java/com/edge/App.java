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
            socketCalidad.bind("tcp://localhost:5110");
            System.out.println("Esperando Alarmas!...");
            while (!Thread.currentThread().isInterrupted()) {
                String alarma = socketCalidad.recvStr();
                System.out.println(alarma);
                socketCalidad.send("Alarma registrada con exito!");
            }
        }
    }
}
