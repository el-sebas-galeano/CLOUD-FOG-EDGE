package com.fog.controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.models.Sensor;
import com.models.SensorTemperatura;

public class SensorManager {
    private List<Sensor> sensorList = new ArrayList<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int numeroProbabilidades =0;
    public SensorManager() {
        // Programar la tarea para ejecutarse cada 10 segundos
        scheduler.scheduleAtFixedRate(this::processSensors, 10, 10, TimeUnit.SECONDS);
    }

    public synchronized void addSensor(Sensor sensor) {
        sensorList.add(sensor);
    }

    private synchronized void processSensors() {
        // Copiar los sensores para procesarlos y luego limpiar la lista
        List<Sensor> sensorsToProcess = new ArrayList<>(sensorList);
        sensorList.clear();

        // Realizar cálculos con los datos de los sensores
        String result = calculateResults(sensorsToProcess);

        // Enviar resultados por ZMQ
        sendResults(result);
    }

    private String calculateResults(List<Sensor> sensors) {
        System.err.println("+++++++++++++====+Calculando promedio+++++=====++++++");
        
        float promedio=0;
        for (Sensor sensor : sensors) {
            promedio += ((SensorTemperatura)sensor).getTemperatura();
        }
        promedio = promedio/sensors.size();
        numeroProbabilidades++;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String mensaje = "C ID: Calculo-Temperatura-"+numeroProbabilidades+" PromedioTemperatura: "+ Float.toString(promedio)+" Hora: "+formattedDateTime;
        System.out.println(mensaje);
        return mensaje;
    }

    private void sendResults(String result) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket pushSocket = context.createSocket(SocketType.PUSH);
            pushSocket.connect("tcp://localhost:5120");
            pushSocket.send(result);
        }
    }
}
