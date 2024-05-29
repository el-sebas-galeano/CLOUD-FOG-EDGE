package com.fog;

import java.util.ArrayList;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.fog.controller.ControllerDatos;
import com.fog.controller.SensorManager;

public class App {

    ArrayList<String> medicionesHumo = new ArrayList<>();
    ArrayList<String> medicionesTemperatura = new ArrayList<>();
    ArrayList<String> medicionesHumedad = new ArrayList<>();    

    public static void main( String[] args ){
        SensorManager sensorManager = new SensorManager();
        ControllerDatos controllerDatos = new ControllerDatos(sensorManager);
        new Thread(controllerDatos).start();
    }
}
