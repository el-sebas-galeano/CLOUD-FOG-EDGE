package com.edge;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class App 
{
    public static void main( String[] args )
    {
        try(ZContext context = new ZContext()){
            Socket socketCalidad=context.createSocket(SocketType.REP);
            Socket socketProxy = context.createSocket(SocketType.PUSH);
            socketCalidad.bind("tcp://*:"+Direcciones.PUERTO_EDGECALIDAD_SENSOR);
            socketProxy.connect("tcp://localhost:"+Direcciones.PUERTO_LOAD_BALANCER_PULL);
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
