package com.edge.control;


import java.time.LocalDateTime;
import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.edge.model.Sensor;
import com.edge.model.SensorHumo;
import com.edge.view.ImpInterfaceSensor;
import com.edge.view.InterfaceSensor;

import zmq.ZMQ;

public class ControllerSensorHumo implements Runnable{

    Sensor sensorInfo = new SensorHumo();
    InterfaceSensor interfaceSensor = new ImpInterfaceSensor();
    Socket socketPush;

    public ControllerSensorHumo(int posicion ,String idSensor, LocalDateTime localDateTime){
        sensorInfo = new SensorHumo(idSensor, localDateTime);
        try(ZContext context = new ZContext()){
            this.socketPush = context.createSocket(SocketType.PUSH);
            socketPush.connect("tcp://localhost:5557");
        }
    };

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
                
                ((SensorHumo) this.sensorInfo).setDetectorHumo(random.nextBoolean());
                this.sensorInfo.setLocalDateTime(LocalDateTime.now());
                interfaceSensor.imprimir(this.sensorInfo.toString());
                if (((SensorHumo) this.sensorInfo).isDetectorHumo()) {
                    activarActuador(this.sensorInfo);
                }

                // Enviar Info a Proxy
                
                // Enviar Info a SC

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException interruptedException) {
                    System.err.println("El hilo falló.");
                }
            }
    }
     
    public void activarActuador(Sensor sensor){
        socketPush.send("El actuador ha sido activado por el sensor " + sensor.getIdSensor().getBytes(ZMQ.CHARSET), 0); 
        interfaceSensor.imprimir("Actuador Activado.");
    }
}
