package com.edge.control;

import java.time.LocalDateTime;
import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;

import com.models.SensorTemperatura;



public class ControllerSensorTemperatura extends ControllerSensor{

    char tipo = ' ';
    private static final float MIN_RANGO = 11.0f;
    private static final float MAX_RANGO = 29.4f;
    private Socket socketPushFog;


    public ControllerSensorTemperatura (char tipo, String idSensor, LocalDateTime localDateTime){
        this.tipo = tipo;
        sensorInfo = new SensorTemperatura(idSensor, localDateTime);
        this.socketPushFog = context.createSocket(SocketType.PUSH);
        this.socketPushFog.connect("tcp://10.43.100.126:5120");
    }

    @Override
    public void run() {
        while (true) {
            ((SensorTemperatura) this.sensorInfo).setTemperatura(generarValor(this.tipo));
            this.sensorInfo.setLocalDateTime(LocalDateTime.now());
            interfaceSensor.imprimir(this.sensorInfo.toString());
            if(tipo == 'F'){
                generarAlarma(((SensorTemperatura) this.sensorInfo).getTemperatura());
            }

            enviarMensaje(socketPushFog, "M " + this.sensorInfo.toString());

            try{
                Thread.sleep(6000);
            } catch (InterruptedException interruptedException){
                System.err.println("El hilo falló.");
            }
        }
    }

    public static float generarValor(char tipo) {
        if (tipo == 'A') {
            return generarDentroDelRango();
        } else if (tipo == 'F') {
            return generarFueraDelRango();
        } else {
            return generarInvalido();
        }
    }

    private static float generarDentroDelRango() {
        Random random = new Random();
        return MIN_RANGO + (MAX_RANGO - MIN_RANGO) * random.nextFloat();
    }

    private static float generarFueraDelRango() {
        Random random = new Random();
        float fuera;
        if (random.nextBoolean()) {
            fuera = random.nextFloat() * MIN_RANGO;
        } else {
            fuera = MAX_RANGO + random.nextFloat() * (50f - MAX_RANGO);
        }
        return fuera;
    }

    private static float generarInvalido() {
        Random random = new Random();
        return -random.nextFloat() * 100.0f;
    }
}
