package com.fog.controller;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class ControllerAlarmasBalancer implements Runnable{

    @Override
    public void run() {
        try(ZContext context = new ZContext()){
            Socket socketEnviador = context.createSocket(SocketType.PUSH); 
            Socket socketRecibidor = context.createSocket(SocketType.REP);
            socketEnviador.connect("tcp://localhost:"+Direcciones.PUERTO_LOAD_BALANCER_PULL);
            socketRecibidor.bind("tcp://*:"+Direcciones.PUERTO_LSERVER_FOGCALIDAD);
            System.out.println("Esperando Alarmas!...");
            while(!Thread.currentThread().isInterrupted()){
                String alarma = socketRecibidor.recvStr();
                System.out.println(alarma);
                socketEnviador.send(alarma);
                socketRecibidor.send("Alarma registrada con exito!");
            }
        }
    }
    
}
