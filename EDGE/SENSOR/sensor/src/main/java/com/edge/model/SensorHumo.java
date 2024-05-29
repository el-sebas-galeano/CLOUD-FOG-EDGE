package com.edge.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensorHumo extends Sensor {
    
    private boolean detectorHumo;

    public SensorHumo() {
        super();
    }

    public SensorHumo(String idSensor, LocalDateTime localDateTime) {
        super(idSensor, localDateTime);
    }

    public boolean isDetectorHumo() {
        return detectorHumo;
    }

    public void setDetectorHumo(boolean detectorHumo) {
        this.detectorHumo = detectorHumo;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = super.getLocalDateTime().format(formatter);
        return  "ID: " + super.getIdSensor() +
                " Stat: " + detectorHumo +
                " Hora: " + formattedDateTime;
    }
}
