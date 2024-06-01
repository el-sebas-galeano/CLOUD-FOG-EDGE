package com.edge.control;

import java.time.LocalDateTime;
import java.util.Random;

import javax.sound.sampled.FloatControl;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;

import com.models.SensorTemperatura;
import com.models.direcciones.Direcciones;
import com.rangos.Rangos;



public class ControllerSensorTemperatura extends ControllerSensor{

    char tipo = ' ';
    private Socket socketPushFog;


    public ControllerSensorTemperatura (char tipo, String idSensor, LocalDateTime localDateTime){
        this.tipo = tipo;
        sensorInfo = new SensorTemperatura(idSensor, localDateTime);
        this.socketPushFog = context.createSocket(SocketType.PUSH);
        this.socketPushFog.connect("tcp://"+Direcciones.DIRECCION_IP_LOAD_BALANCER+":"+Direcciones.DIRECCION_IP_LOAD_BALANCER);
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
        return Float.parseFloat(Rangos.MIN_TEMPERATURA) + (Float.parseFloat(Rangos.MAX_TEMPERATURA) - Float.parseFloat( Rangos.MIN_TEMPERATURA)) * random.nextFloat();
    }

    private static float generarFueraDelRango() {
        Random random = new Random();
        float fuera;
        if (random.nextBoolean()) {
            fuera = random.nextFloat() * Float.parseFloat(Rangos.MIN_TEMPERATURA);
        } else {
            fuera = Float.parseFloat(Rangos.MAX_TEMPERATURA) + random.nextFloat() * (50f - (Float.parseFloat(Rangos.MAX_TEMPERATURA)));
        }
        return fuera;
    }

    private static float generarInvalido() {
        Random random = new Random();
        return -random.nextFloat() * 100.0f;
    }
}
