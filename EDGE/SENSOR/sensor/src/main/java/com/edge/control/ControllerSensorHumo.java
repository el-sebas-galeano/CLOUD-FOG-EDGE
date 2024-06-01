package com.edge.control;

import java.time.LocalDateTime;
import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;

import com.models.Sensor;
import com.models.SensorHumo;

public class ControllerSensorHumo extends ControllerSensor {

    private Socket socketPushActivator;
    private Socket socketPushFog;

    public ControllerSensorHumo(int posicion, String idSensor, LocalDateTime localDateTime) {
        this.sensorInfo = new SensorHumo(idSensor, localDateTime);
        this.socketPushActivator = context.createSocket(SocketType.PUSH);
        this.socketPushFog = context.createSocket(SocketType.PUSH);
        socketPushActivator.connect("tcp://localhost:5100");
        socketPushFog.connect("tcp://10.43.100.126:5120");
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            ((SensorHumo) this.sensorInfo).setDetectorHumo(random.nextBoolean());
            this.sensorInfo.setLocalDateTime(LocalDateTime.now());
            interfaceSensor.imprimir(this.sensorInfo.toString());

            enviarMensaje(socketPushFog, "M " + this.sensorInfo.toString());

            if (((SensorHumo) this.sensorInfo).isDetectorHumo()) {
                activarActuador(this.sensorInfo);
                generarAlarma();
            }

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
        enviarMensaje(socketPushActivator, mensaje);
        interfaceSensor.imprimir("Actuador Activado.");
    }

    // Método para cerrar el contexto y el socket al finalizar
    public void close() {
        if (socketPushActivator != null) {
            socketPushActivator.close();
        }
        if (context != null) {
            context.close();
        }
    }
}