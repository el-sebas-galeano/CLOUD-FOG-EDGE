package com.fog.controller;

import java.util.concurrent.TimeUnit;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.models.direcciones.Direcciones;

import org.zeromq.ZMQException;

public class ControllerHeartBeat implements Runnable {

    private static final int MAX_RETRIES = 3; // Número máximo de reintentos
    private Socket socketNotifiacion;

    @Override
    public void run() {
        try (ZContext context = new ZContext()) {
            Socket socketBeat = createSocket(context);
            socketNotifiacion = context.createSocket(SocketType.REQ);
            socketNotifiacion.connect("tcp://"+Direcciones.DIRECCION_IP_PROXYA+":"+Direcciones.PUERTO_MONITOR_PROXY_NOTIFICACION);
            String mensaje = "Ok";
            int retriesLeft = MAX_RETRIES;

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socketBeat.send(mensaje);
                    String latido = socketBeat.recvStr();
                    if (latido != null && latido.startsWith("Ok")) {
                        System.out.println("Latido recibido: " + latido);
                        retriesLeft = MAX_RETRIES; // Resetear los reintentos después de una respuesta exitosa
                    } else {
                        retriesLeft--;
                        System.out.println("Latido no recibido. Intentos restantes: " + retriesLeft);
                        if (retriesLeft <= 0) {
                            failure();
                            retriesLeft = MAX_RETRIES; // Resetear el contador después de manejar el fallo
                        }
                    }
                } catch (ZMQException e) {
                    System.out.println("Error de comunicación: " + e.getMessage());
                    socketBeat = recreateSocket(context, socketBeat);
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Error interrupted");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Socket createSocket(ZContext context) {
        Socket socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://"+Direcciones.DIRECCION_IP_PROXYA+":"+Direcciones.PUERTO_MONITOR_PROXY_BEAT);
        socket.setReceiveTimeOut(5000);
        return socket;
    }

    private Socket recreateSocket(ZContext context, Socket oldSocket) {
        oldSocket.close();
        return createSocket(context);
    }

    private void failure() {
        System.out.println("Máximo número de reintentos alcanzado. Fallo detectado.");
        socketNotifiacion.send("failure"+ " tcp://localhost:5240");
    }
}
