package com.edge.control;

import java.time.LocalDateTime;
import java.util.Random;

import com.edge.model.Sensor;
import com.edge.model.SensorHumedad;
import com.edge.view.ImpInterfaceSensor;
import com.edge.view.InterfaceSensor;

public class ControllerSensorHumedad extends ControllerSensor {

    private char tipo = ' ';
    private static final int MIN_RANGO = 70;
    private static final int MAX_RANGO = 100;
    
    public ControllerSensorHumedad(char tipo, String idSensor, LocalDateTime localDateTime) {
        this.tipo = tipo;
        sensorInfo = new SensorHumedad(idSensor, localDateTime);
    }

    @Override
    public void run() {
        while (true) {
            ((SensorHumedad) this.sensorInfo).setHumedad(generarValor(this.tipo));
            this.sensorInfo.setLocalDateTime(LocalDateTime.now());
            interfaceSensor.imprimir(this.sensorInfo.toString());
            try{
                Thread.sleep(5000);
            }catch(InterruptedException interruptedException){
                System.err.println("El hilo falló.");
            }
        }

    }

    public static int generarValor(char tipo) {
        if (tipo == 'A') {
            return generarDentroDelRango();
        } else if (tipo == 'F') {
            return generarFueraDelRango();
        } else {
            return generarInvalido();
        }
    }

    private static int generarDentroDelRango() {
        Random random = new Random();
        return MIN_RANGO + random.nextInt((MAX_RANGO - MIN_RANGO) + 1);
    }

    private static int generarFueraDelRango() {
        Random random = new Random();
        return random.nextInt(MIN_RANGO);
        
    }

    private static int generarInvalido() {
        Random random = new Random();
        return -random.nextInt(100);
    }
}
