package com.cloud.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger{

    private String archivo;
    
    public Logger(String archivo) {
        this.archivo = archivo;
    }
    public void log(String mensaje) {
        try (FileWriter fileWriter = new FileWriter(archivo, true);
            PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
