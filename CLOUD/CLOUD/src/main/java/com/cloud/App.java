package com.cloud;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.cloud.utils.Logger;

public class App 
{
    public static void main( String[] args )
    {
        Logger fileAlarmas = new Logger("alarmas.txt");
        Logger fileMediciones = new Logger("mediciones.txt");
        Logger fileCalculos = new Logger("calculos.txt");
        try(ZContext context = new ZContext()){
            Socket socketCloud = context.createSocket(SocketType.REP);
            socketCloud.bind("tcp://localhost:5230");
            System.out.println("Esperando mensajes del proxy!...");
            while (!Thread.currentThread().isInterrupted()) {
                String data = socketCloud.recvStr();
                System.out.println(data);
                if(data.startsWith("A")){
                    fileAlarmas.log(data);
                }else if(data.startsWith("M")){
                    fileMediciones.log(data);
                }else if(data.startsWith("C")){
                    fileCalculos.log(data);
                }else{
                    System.out.println("Mensaje no reconocido: "+data);
                }
                socketCloud.send("Data guardada con exito en cloud!...");
            }
        }
    }
}
