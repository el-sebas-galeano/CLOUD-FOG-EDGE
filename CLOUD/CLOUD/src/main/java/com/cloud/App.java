package com.cloud;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.cloud.control.DataManager;
import com.cloud.utils.Logger;
import com.models.direcciones.Direcciones;

public class App 
{
    public static void main( String[] args ){
        DataManager dataManager = new DataManager();
        Logger fileAlarmas = new Logger("alarmas.txt");
        Logger fileMediciones = new Logger("mediciones.txt");
        Logger fileCalculos = new Logger("calculos.txt");
        try(ZContext context = new ZContext()){
            Socket socketCloud = context.createSocket(SocketType.REP);
            socketCloud.bind("tcp://*:"+Direcciones.PUERTO_PROXY_CLOUD);
            System.out.println("Esperando mensajes del proxy!...");
            while (!Thread.currentThread().isInterrupted()) {
                String data = socketCloud.recvStr();
                 if(data.startsWith("C")){
                    System.out.println(data);
                 }
                if(data.startsWith("A")){
                    fileAlarmas.log(data);
                }else if(data.startsWith("M")){
                    fileMediciones.log(data);
                }else if(data.startsWith("C")){
                    fileCalculos.log(data);
                    float medicion = procesarData(data);
                    if(medicion > 0){
                        dataManager.addMedicion(medicion);
                    }
                }else{
                    System.out.println("Mensaje no reconocido: "+data);
                }
                socketCloud.send("Data guardada con exito en cloud!...");
            }
        }
    }

    private static float procesarData(String data) {
        String[] parts = data.split(" ");
        boolean isHumedad = parts[3].startsWith("PromedioHumedad");
        float value = Float.parseFloat(parts[4]);
        if(isHumedad){
            return value;
        }
        return -1;
    }
}
