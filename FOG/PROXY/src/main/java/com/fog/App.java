package com.fog;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class App {

    private static final int PUERTO = 5200; 
    public static void main( String[] args ){

        if (args.length != 1) {
            System.out.println("El unico argumento valido es la direccion IP.");
            System.exit(1);
        }

        try (ZContext context = new ZContext()) {
            Socket pullSocket = context.createSocket(SocketType.PULL);
            pullSocket.bind("tcp://" + args[0] + ":" + PUERTO);
            while (true) {
                System.out.println("Esperando......");
                String data = pullSocket.recvStr();
                System.out.println(data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
