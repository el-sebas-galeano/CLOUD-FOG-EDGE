package com.fog;

import com.fog.controller.ControllerHeartBeat;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ControllerHeartBeat controllerHeartBeat = new ControllerHeartBeat();
        new Thread(controllerHeartBeat).start();
    }
}
