package com.edge.control;

import java.time.LocalDateTime;
import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.edge.model.Sensor;
import com.edge.model.SensorHumo;
import com.edge.view.ImpInterfaceSensor;
import com.edge.view.InterfaceSensor;

public class ControllerSensorHumo implements Runnable {

    private Sensor sensorInfo;
    private InterfaceSensor interfaceSensor;
    private ZContext context;
    private Socket socketPush;

    public ControllerSensorHumo(int posicion, String idSensor, LocalDateTime localDateTime) {
        this.sensorInfo = new SensorHumo(idSensor, localDateTime);
        this.interfaceSensor = new ImpInterfaceSensor();
        this.context = new ZContext();
        this.socketPush = context.createSocket(SocketType.PUSH);
        socketPush.connect("tcp://localhost:5557");
    }

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

    public void activarActuador(Sensor sensor) {
        String mensaje = "El actuador ha sido activado por el sensor " + sensor.getIdSensor();
        socketPush.send(mensaje.getBytes(ZMQ.CHARSET), 0);
        interfaceSensor.imprimir("Actuador Activado.");
    }

    // Método para cerrar el contexto y el socket al finalizar
    public void close() {
        if (socketPush != null) {
            socketPush.close();
        }
        if (context != null) {
            context.close();
        }
    }
}