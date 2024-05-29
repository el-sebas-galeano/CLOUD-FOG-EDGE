package com.edge.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensorTemperatura extends Sensor{  
    private float temperatura;

    public SensorTemperatura(){
        super();
    }

    public SensorTemperatura(String idSensor, LocalDateTime localDateTime) {
        super(idSensor, localDateTime);
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = super.getLocalDateTime().format(formatter);
        return "ID: " + super.getIdSensor() +
                " Temp: " + temperatura +
                " Hora: " + formattedDateTime;
    }
}
