package com.fog;

import java.util.ArrayList;
import java.util.List;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class App {

    private static final int PUERTO_EDGE = 5120;
    private static final int PUERTO_FOG = 5200;
    public static void main( String[] args ){
        try (ZContext context = new ZContext()) {
            Socket pullSocket = context.createSocket(SocketType.PULL);
            pullSocket.bind("tcp://*:" + PUERTO_EDGE);

            Socket pushSocket = context.createSocket(SocketType.PUSH);
            pushSocket.connect("tcp://localhost:" + PUERTO_FOG);

            while (true) {
                System.out.println("Esperando.......");
                String data = pullSocket.recvStr();
                System.out.println("Received data: " + data);
                pushSocket.send(data);
                System.out.println("Sending data: " + data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

