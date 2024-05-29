package com.edge.control;

import java.time.LocalDateTime;
import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;

import com.edge.model.Sensor;
import com.edge.model.SensorHumo;


public class ControllerSensorHumo extends ControllerSensor {

    private Socket socketPush;

    public ControllerSensorHumo(int posicion, String idSensor, LocalDateTime localDateTime) {
        this.sensorInfo = new SensorHumo(idSensor, localDateTime);
        this.socketPush = context.createSocket(SocketType.PUSH);
        socketPush.connect("tcp://localhost:5100");
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
        enviarMensaje(socketPush, mensaje);
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