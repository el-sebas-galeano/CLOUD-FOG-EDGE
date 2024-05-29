package com.edge.control;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.edge.view.ImpInterfaceSensor;
import com.edge.view.InterfaceSensor;

public class ControllerArchivoConfiguracion {

    private float dentro, fuera, invalido;
    InterfaceSensor interfaceSensor = new ImpInterfaceSensor();

    public ControllerArchivoConfiguracion(String nombreArchivo) {

        try (BufferedReader br = new BufferedReader(new FileReader("configuracion.txt"))) {
            dentro = Float.parseFloat(br.readLine());
            fuera = Float.parseFloat(br.readLine());
            invalido = Float.parseFloat(br.readLine());
        } catch (FileNotFoundException fileNotFoundException) {
            this.interfaceSensor.imprimir(fileNotFoundException.getMessage());
        } catch (NumberFormatException numberFormatException) {
            this.interfaceSensor.imprimir(numberFormatException.getMessage());
        } catch (IOException ioException) {
            this.interfaceSensor.imprimir(ioException.getMessage());
        }
    }

    public float getDentro() {
        return dentro;
    }

    public void setDentro(float dentro) {
        this.dentro = dentro;
    }

    public float getFuera() {
        return fuera;
    }

    public void setFuera(float fuera) {
        this.fuera = fuera;
    }

    public float getInvalido() {
        return invalido;
    }

    public void setInvalido(float invalido) {
        this.invalido = invalido;
    }

    
}
