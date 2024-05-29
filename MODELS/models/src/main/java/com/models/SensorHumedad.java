package com.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensorHumedad extends Sensor{
    private int humedad;

    public SensorHumedad(){
        super();
    }

    public SensorHumedad(String idSensor, LocalDateTime localDateTime) {
        super(idSensor, localDateTime);
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        this.humedad = humedad;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = super.getLocalDateTime().format(formatter);
        return  "ID: " + super.getIdSensor() +
                " Hdad: " + humedad +
                " Hora: " + formattedDateTime;
    }

}
