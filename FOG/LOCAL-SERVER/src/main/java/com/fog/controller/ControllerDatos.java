package com.fog.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.Sensor;
import com.models.SensorHumedad;
import com.models.SensorHumo;
import com.models.SensorTemperatura;


public class ControllerDatos implements Runnable {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final float MIN_RANGO= (float) 11;
    private SensorManager sensorManager;
    public ControllerDatos(SensorManager sensorManager){
        this.sensorManager= sensorManager;
    }
    @Override
    public void run() {
        try(ZContext context = new ZContext()){
            Socket socketProxy = context.createSocket(SocketType.PULL);
            socketProxy.bind("tcp://*:5220");

            while (true) {
                String data = socketProxy.recvStr();
                Sensor s =procesarData(data);
                if(s!=null){
                    sensorManager.addSensor(s);
                }
            }
        }
    }
    public Sensor procesarData(String data) {
        String[] parts = data.split(" ");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Formato de datos incorrecto: " + data);
        }

        String id = parts[2]; 
        LocalDateTime hora = LocalDateTime.parse(parts[6] + " " + parts[7], formatter);

        switch (id.split("-")[1]) {
            case "HDAD":
                int humedad = Integer.parseInt(parts[4]);
                SensorHumedad sensorHumedad = new SensorHumedad(id, hora);
                sensorHumedad.setHumedad(humedad);
                return sensorHumedad;

            case "HUMO":
                boolean stat = Boolean.parseBoolean(parts[3]); 
                SensorHumo sensorHumo = new SensorHumo(id, hora);
                sensorHumo.setDetectorHumo(stat);
                return sensorHumo;

            case "TEMP":
                float temperatura = Float.parseFloat(parts[4]);
                SensorTemperatura sensorTemperatura = new SensorTemperatura(id, hora);
                sensorTemperatura.setTemperatura(temperatura);
                return sensorTemperatura;

            default:
                throw new IllegalArgumentException("Tipo de sensor desconocido: " + id);
        }
    }

}
