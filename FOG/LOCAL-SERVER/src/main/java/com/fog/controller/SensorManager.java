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
    private List<Sensor> listadoSensores = new ArrayList<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int contadorCalculos = 0;

    public SensorManager() {
        // Programar la tarea para ejecutarse cada 10 segundos
        scheduler.scheduleAtFixedRate(this::procesarMediciones, 0, 10, TimeUnit.SECONDS);
    }

    public synchronized void addSensor(Sensor sensor) {
        listadoSensores.add(sensor);
    }

    private synchronized void procesarMediciones() {
        // Copiar los sensores para procesarlos y luego limpiar la lista
        List<Sensor> sensores = new ArrayList<>(listadoSensores);
        listadoSensores.clear();

        // Realizar cálculos con los datos de los sensores
        if (!sensores.isEmpty()) {
            if (sensores.size() > 10) {
                while (sensores.size() > 10) {
                    List<Sensor> subLista = sensores.subList(0, 10);
                    String resultado = calcularResultados(subLista);
                    enviarResultados(resultado);
                    // Eliminar los sensores procesados del listado
                    sensores.subList(0, 10).clear();
                }
            } else {
                String resultadoRestante = calcularResultados(sensores);
                if (resultadoRestante != "") {
                    enviarResultados(resultadoRestante);
                }
                sensores.clear();
            }
        }
    }

    private String calcularResultados(List<Sensor> sensors) {
        if (!sensors.isEmpty()) {
            System.err.println("Calculando Promedio...");

            float promedio = 0;
            for (Sensor sensor : sensors) {
                promedio += ((SensorTemperatura) sensor).getTemperatura();
            }
            promedio = promedio / sensors.size();
            contadorCalculos++;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            String mensaje = "C ID: Calculo-Temperatura-" + contadorCalculos + " PromedioTemperatura: "
                    + Float.toString(promedio) + " Hora: " + formattedDateTime;
            System.out.println(mensaje);
            return mensaje;
        }
        return "";
    }

    private void enviarResultados(String resultado) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket pushSocket = context.createSocket(SocketType.PUSH);
            pushSocket.connect("tcp://localhost:5120");
            pushSocket.send(resultado);
        }
    }
}
