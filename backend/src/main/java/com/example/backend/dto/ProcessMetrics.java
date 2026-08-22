package com.example.backend.dto;

import lombok.Data;

public class ProcessMetrics {
    private int procesosActuales;     // p1
    private int procesosMaximos;      // p2
    private int sesionesActuales;     // p3
    private int sesionesActivas;      // p4
    private int sesionesInactivas;    // p5
    private int sesionesBloqueadas;   // p6
    private int operacionesProlongadas; // p7
    private int usoRecursos;

    public int getProcesosActuales() {
        return procesosActuales;
    }

    public void setProcesosActuales(int procesosActuales) {
        this.procesosActuales = procesosActuales;
    }

    public int getProcesosMaximos() {
        return procesosMaximos;
    }

    public void setProcesosMaximos(int procesosMaximos) {
        this.procesosMaximos = procesosMaximos;
    }

    public int getSesionesActuales() {
        return sesionesActuales;
    }

    public void setSesionesActuales(int sesionesActuales) {
        this.sesionesActuales = sesionesActuales;
    }

    public int getSesionesActivas() {
        return sesionesActivas;
    }

    public void setSesionesActivas(int sesionesActivas) {
        this.sesionesActivas = sesionesActivas;
    }

    public int getSesionesInactivas() {
        return sesionesInactivas;
    }

    public void setSesionesInactivas(int sesionesInactivas) {
        this.sesionesInactivas = sesionesInactivas;
    }

    public int getSesionesBloqueadas() {
        return sesionesBloqueadas;
    }

    public void setSesionesBloqueadas(int sesionesBloqueadas) {
        this.sesionesBloqueadas = sesionesBloqueadas;
    }

    public int getOperacionesProlongadas() {
        return operacionesProlongadas;
    }

    public void setOperacionesProlongadas(int operacionesProlongadas) {
        this.operacionesProlongadas = operacionesProlongadas;
    }

    public int getUsoRecursos() {
        return usoRecursos;
    }

    public void setUsoRecursos(int usoRecursos) {
        this.usoRecursos = usoRecursos;
    }
}
