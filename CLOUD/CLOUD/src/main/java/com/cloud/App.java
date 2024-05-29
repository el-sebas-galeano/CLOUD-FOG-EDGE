package com.cloud;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.cloud.utils.Logger;

public class App 
{
    public static void main( String[] args )
    {
        Logger file = new Logger("data.txt");
        try(ZContext context = new ZContext()){
            Socket socketCloud = context.createSocket(SocketType.REP);
            socketCloud.bind("tcp://localhost:5230");
            System.out.println("Esperando mensajes del proxy!...");
            while (!Thread.currentThread().isInterrupted()) {
                String data = socketCloud.recvStr();
                file.log(data);
                socketCloud.send("Data guardada con exito en cloud!...");
            }
        }
    }
}
