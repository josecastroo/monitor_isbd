package com.example.backend.dto;

import lombok.Data;

public class ProcessMetrics {
    private Integer procesosActuales;
    private Integer limiteProcesos;
    private Integer sesionesTotales;
    private Integer sesionesActivas;
    private Integer sesionesBloqueadas;

    public Integer getProcesosActuales() {
        return procesosActuales;
    }

    public void setProcesosActuales(Integer procesosActuales) {
        this.procesosActuales = procesosActuales;
    }

    public Integer getLimiteProcesos() {
        return limiteProcesos;
    }

    public void setLimiteProcesos(Integer limiteProcesos) {
        this.limiteProcesos = limiteProcesos;
    }

    public Integer getSesionesTotales() {
        return sesionesTotales;
    }

    public void setSesionesTotales(Integer sesionesTotales) {
        this.sesionesTotales = sesionesTotales;
    }

    public Integer getSesionesActivas() {
        return sesionesActivas;
    }

    public void setSesionesActivas(Integer sesionesActivas) {
        this.sesionesActivas = sesionesActivas;
    }

    public Integer getSesionesBloqueadas() {
        return sesionesBloqueadas;
    }

    public void setSesionesBloqueadas(Integer sesionesBloqueadas) {
        this.sesionesBloqueadas = sesionesBloqueadas;
    }
}
