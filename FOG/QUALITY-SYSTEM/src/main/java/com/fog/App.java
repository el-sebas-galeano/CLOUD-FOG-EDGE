package com.fog;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.fog.controller.ControllerAlarmasBalancer;

public class App 
{
    public static void main( String[] args )
    {
        ControllerAlarmasBalancer controllerAlarmasBalancer = new ControllerAlarmasBalancer();
        new Thread(controllerAlarmasBalancer).start();
    }
}
