package com.edge.model;

import java.time.LocalDateTime;

public abstract class Sensor {

    private String idSensor;
    private LocalDateTime localDateTime;
    
    public Sensor() {
    }

    public Sensor(String idSensor, LocalDateTime localDateTime) {
        this.idSensor = idSensor;
        this.localDateTime = localDateTime;
    }

    public String getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(String idSensor) {
        this.idSensor = idSensor;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

}
