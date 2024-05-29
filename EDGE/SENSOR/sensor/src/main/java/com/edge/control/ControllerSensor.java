package com.edge.control;

import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.edge.model.Sensor;
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
}
