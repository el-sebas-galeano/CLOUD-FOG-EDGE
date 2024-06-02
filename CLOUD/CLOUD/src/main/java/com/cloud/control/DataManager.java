package com.cloud.control;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

public class DataManager {
    private List<Float> listadoMedicionesHumedad = new ArrayList<>();

    int contadorCalculos = 0;
    Socket socketQuality;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DataManager() {
        ZContext context = new ZContext();
        this.socketQuality = context.createSocket(SocketType.REQ);
        this.socketQuality.connect("tcp://"+Direcciones.DIRECCION_IP_CLOUD_CALIDAD+":"+Direcciones.PUERTO_CLOUDCALIDAD_CLOUD);
        scheduler.scheduleAtFixedRate(this::procesarMedicionesHumedad, 0, 20, TimeUnit.SECONDS);
    }

    public synchronized void addMedicion(Float calculo) {
        this.listadoMedicionesHumedad.add(calculo);
    }

    private synchronized void procesarMedicionesHumedad() {
        List<Float> mediciones = new ArrayList<>(this.listadoMedicionesHumedad);
        this.listadoMedicionesHumedad.clear();

        if (!mediciones.isEmpty()) {
            if (mediciones.size() > 4) {
                while (mediciones.size() > 4) {
                    List<Float> sublista = mediciones.subList(0, 4);
                    String resultado = calcularResultados(sublista);
                    System.out.println("Humedad Mensual: " + resultado);
                    mediciones.subList(0, 4).clear();
                }
            } else {
                String resultadoRestante = calcularResultados(mediciones);
                if (resultadoRestante != "") {
                    System.out.println(resultadoRestante);
                    mediciones.clear();
                }
            }
        }

    }

    private String calcularResultados(List<Float> sublista) {
        if(!sublista.isEmpty()){
            float promedio = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            for(float medicion: sublista){
                promedio += medicion;
            }
            promedio = promedio / sublista.size();
            contadorCalculos++;
            if(promedio < 70.0){
                enviarAlarma("A Humedad Mensual fuera de los rangos establecidos!" );
            }
            return "C ID: HM-" + contadorCalculos + " Humedad Mensual: " + promedio + " Hora: " + formattedDateTime;
        }
        return "";
    }

    private void enviarAlarma(String alarma) {
        this.socketQuality.send(alarma);
        String recibido = this.socketQuality.recvStr();
        System.out.println(recibido);
    }
}
