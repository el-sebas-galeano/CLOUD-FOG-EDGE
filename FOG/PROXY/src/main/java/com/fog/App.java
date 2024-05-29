package com.fog;


import com.fog.Controller.ControllerProxy;

public class App {

    private static final int PUERTO = 5200; 

    public static void main( String[] args ){

        if (args.length != 1) {
            System.out.println("El unico argumento valido es la direccion IP.");
            System.exit(1);
        }
        ControllerProxy controllerProxy =  new ControllerProxy("tcp://" + args[0] + ":" + PUERTO);
        new Thread(controllerProxy).start();
    }
}
