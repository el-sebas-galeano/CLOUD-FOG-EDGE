package com.edge.control;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.Sensor;
import com.edge.view.ImpInterfaceSensor;
import com.edge.view.InterfaceSensor;

import zmq.ZMQ;

public abstract class ControllerSensor implements Runnable {
    final InterfaceSensor interfaceSensor = new ImpInterfaceSensor();
    protected Sensor sensorInfo;
    protected ZContext context;
    ControllerSensor(){    
        this.context = new ZContext();       
    }
    public void enviarMensaje(Socket socket, String mensaje){
        socket.send(mensaje.getBytes(ZMQ.CHARSET));
    }

    public String enviarAlarma(String alarma){
        try(Socket socketCalidad = context.createSocket(SocketType.REQ);){
            socketCalidad.connect("tcp://localhost:5110");
            String alarma2= "A "+ sensorInfo.getIdSensor()+ " "+ alarma; 
            socketCalidad.send(alarma2.getBytes());
            
            String respuesta= socketCalidad.recvStr();
            System.out.println(respuesta);
            return respuesta;
        }
    }

    protected String generarAlarma(int medida){
        return enviarAlarma(Integer.toString(medida));
    }
    protected String generarAlarma(float medida){
        return enviarAlarma(Float.toString(medida));
    }
    protected String generarAlarma(){
        return enviarAlarma("Encendido el actuador!");
    }
}
