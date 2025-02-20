package com.fog;


import com.fog.controller.ControllerMonitor;
import com.fog.controller.ControllerProxy;
import com.models.direcciones.Direcciones;

public class App {

    private static String PUERTO; 
    private static final int PUERTO_LOCAL_SERVER = 5220;

    public static void main( String[] args ){

        if (args.length >2) {
            System.out.println("El unico argumento valido es la direccion IP.");
            System.exit(1);
        }
        PUERTO = args[1];
        ControllerProxy controllerProxy =  new ControllerProxy("tcp://*:" +PUERTO, "tcp://" + args[0] + ":" + Direcciones.PUERTO_PROXY_LSERVER);

        new Thread(controllerProxy).start();
        ControllerMonitor controllerMonitor = new ControllerMonitor();
        new Thread(controllerMonitor).start();
    }
}
