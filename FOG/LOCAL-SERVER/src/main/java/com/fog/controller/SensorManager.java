package com.fog.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.models.Sensor;
import com.models.SensorHumedad;
import com.models.SensorTemperatura;
import com.models.direcciones.Direcciones;

public class SensorManager {
    private final float MIN_TEMPERATURA= 11;
    private final float MAX_TEMPERATURA = 29.4f;
    private List<Sensor> listadoSensoresTemperatura = new ArrayList<>();
    private List<Sensor> listadoSensoresHumedad = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int contadorCalculos = 0;

    private Socket socketLoadBalancer;
    private Socket socketQuality;
    public SensorManager() {
        // Programar la tarea para ejecutarse cada 10 segundos
        ZContext context = new ZContext();
        this.socketLoadBalancer = context.createSocket(SocketType.PUSH);
        this.socketQuality =  context.createSocket(SocketType.REQ);
        this.socketLoadBalancer.connect("tcp://"+Direcciones.DIRECCION_IP_LOAD_BALANCER+":"+ Direcciones.PUERTO_LOAD_BALANCER_PULL);
        this.socketQuality.connect("tcp://"+Direcciones.DIRECCION_IP_EDGE_CALIDAD+":"+Direcciones.PUERTO_LSERVER_FOGCALIDAD);

        scheduler.scheduleAtFixedRate(this::procesarMedicionesTemperatura, 0, 10, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::procesarMedicionesHumedad, 0, 5, TimeUnit.SECONDS);
    }

    public synchronized void addSensor(Sensor sensor) {
        if (sensor instanceof SensorTemperatura) {
            listadoSensoresTemperatura.add(sensor);
        } else if (sensor instanceof SensorHumedad) {
            listadoSensoresHumedad.add(sensor);
        }
    }

    private synchronized void procesarMedicionesTemperatura() {
        // Copiar los sensores para procesarlos y luego limpiar la lista
        List<Sensor> sensores = new ArrayList<>(listadoSensoresTemperatura);
        listadoSensoresTemperatura.clear();

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

            float promedio = 0;
            sensors.get(0).getIdSensor();
            for (Sensor sensor : sensors) {
                if (sensor instanceof SensorTemperatura) {
                    promedio += ((SensorTemperatura) sensor).getTemperatura();
                } else if (sensor instanceof SensorHumedad) {
                    promedio += ((SensorHumedad) sensor).getHumedad();
                }
            }
            promedio = promedio / sensors.size();
            contadorCalculos++;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            if (sensors.get(0) instanceof SensorTemperatura) {
                String mensaje = "C ID: CT-" + contadorCalculos + " PromedioTemperatura: "
                    + Float.toString(promedio) + " Hora: " + formattedDateTime;
                    System.out.println();
                    System.out.println("Promedio de Temperatura:");
                    System.out.println(mensaje);
                    System.out.println();
                    if(promedio>MAX_TEMPERATURA|| promedio < MIN_TEMPERATURA){
                        String alarma= "A Promedio temperatura fuera de los rangos establecidos...! "+ Float.toString(promedio);
                        enviarAlarma(alarma);   
                    }
                    return mensaje;
            } else if (sensors.get(0) instanceof SensorHumedad) {
                String mensaje = "C ID: CH-" + contadorCalculos + " PromedioHumedad: "
                    + Float.toString(promedio) + " Hora: " + formattedDateTime;
                    System.out.println();
                    System.out.println("Promedio de Humedad");
                    System.out.println(mensaje);
                    System.out.println();
                    return mensaje;
            }
        }
        return "";
    }
    private void enviarAlarma(String alarma){
        this.socketQuality.send(alarma);
        String recibido = this.socketQuality.recvStr();
        System.out.println(recibido);
    }
    private void enviarResultados(String resultado) {
        this.socketLoadBalancer.send(resultado);
    }

    private void procesarMedicionesHumedad() {
        List<Sensor> sensoresHumedad = new ArrayList<>(listadoSensoresHumedad);
        listadoSensoresHumedad.clear();

        if (!sensoresHumedad.isEmpty()) {
            if (sensoresHumedad.size() > 10) {
                while (sensoresHumedad.size() > 10) {
                    List<Sensor> subLista = sensoresHumedad.subList(0, 10);
                    String resultado = calcularResultados(subLista);
                    enviarResultados(resultado);
                    // Eliminar los sensores procesados del listado
                    subLista.clear();
                }
            } else {
                String resultadoRestante = calcularResultados(sensoresHumedad);
                if (!resultadoRestante.isEmpty()) {
                    enviarResultados(resultadoRestante);
                }
                sensoresHumedad.clear();
            }
        }
    }
}
