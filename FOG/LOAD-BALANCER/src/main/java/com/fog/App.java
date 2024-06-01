package com.fog;

import java.util.ArrayList;
import java.util.List;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import com.fog.controller.ControllerLoadBalancer;

public class App {

    public static void main( String[] args ){
        ControllerLoadBalancer controllerLoadBalancer = new ControllerLoadBalancer();
        new Thread(controllerLoadBalancer).start(); 
    }
}

