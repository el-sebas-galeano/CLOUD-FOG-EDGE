package com.edge;

import java.time.LocalDateTime;

import com.edge.control.ControllerArchivoConfiguracion;
import com.edge.control.ControllerSensorHumedad;
import com.edge.control.ControllerSensorHumo;
import com.edge.control.ControllerSensorTemperatura;
import com.edge.view.ImpInterfaceSensor;
import com.edge.view.InterfaceSensor;

public class App {
    public static void main(String[] args) {

        InterfaceSensor interfaceSensor = new ImpInterfaceSensor();

        if (args.length < 2) {
            interfaceSensor.imprimir("Número de argumentos inválido.");
            System.exit(1);
        }
        ControllerArchivoConfiguracion controllerArchivoConfiguracion = new ControllerArchivoConfiguracion(args[1]);

        int cantidadDentro = 0, cantidadFuera = 0, cantidadInvalidos = 0;
        cantidadDentro = Math.round(controllerArchivoConfiguracion.getDentro() * 10);
        cantidadFuera = Math.round(controllerArchivoConfiguracion.getFuera() * 10);
        cantidadInvalidos = Math.round(controllerArchivoConfiguracion.getInvalido() * 10);

        interfaceSensor.inicio();

        switch (args[0]) {
            case "humo":
            case "Humo":
            case "HUMO":
            case "H":
            case "h":
                interfaceSensor.imprimir("Iniciando Sensor de Humo.");
                for (int contador = 0; contador < 10; contador++) {
                    ControllerSensorHumo sensorController = new ControllerSensorHumo(contador, "S-HUMO-00" + (contador + 1),
                            LocalDateTime.now());
                    new Thread(sensorController).start();
                }
                break;

            case "humedad":
            case "Humedad":
            case "HUMEDAD":
            case "hd":
            case "HD":
            case "Hd":
                interfaceSensor.imprimir("Iniciando Sensor de Humedad.");
                for(int contador = 0; contador < 10; contador++){
                    ControllerSensorHumedad controllerSensorHumedad;
                    if(contador < cantidadDentro){
                        controllerSensorHumedad = new ControllerSensorHumedad('A', "S-HDAD-00" + (contador + 1), LocalDateTime.now());
                    }else if((contador < cantidadFuera + cantidadDentro) && (contador >= cantidadDentro)){
                        controllerSensorHumedad = new ControllerSensorHumedad('F', "S-HDAD-00" + (contador + 1), LocalDateTime.now());
                    }else{
                        controllerSensorHumedad = new ControllerSensorHumedad('I', "S-HDAD-00" + (contador + 1), LocalDateTime.now());
                    }
                    new Thread(controllerSensorHumedad).start();
                }
                break;

            case "temperatura":
            case "Temperatura":
            case "TEMPERATURA":
            case "T":
            case "t":
                interfaceSensor.imprimir("Iniciando Sensor de Temperatura.");
                for(int contador = 0; contador < 10; contador++){
                    ControllerSensorTemperatura controllerSensorTemperatura;
                    if(contador < cantidadDentro){
                        controllerSensorTemperatura = new ControllerSensorTemperatura('A', "S-TEMP-00" + (contador + 1), LocalDateTime.now());
                    } else if((contador < cantidadFuera + cantidadDentro) && (contador >= cantidadDentro)){
                        controllerSensorTemperatura = new ControllerSensorTemperatura('F', "S-TEMP-00" + (contador + 1), LocalDateTime.now());
                    } else {
                        controllerSensorTemperatura = new ControllerSensorTemperatura('I', "S-TEMP-00" + (contador + 1), LocalDateTime.now());
                    }
                    new Thread(controllerSensorTemperatura).start();
                }
                break;

            default:
                interfaceSensor.imprimir("No hay ningun sensor de tipo " + args[0]);
                System.exit(1);
                break;
        }
    }
}
