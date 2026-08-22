package com.example.backend.dto;

import lombok.Data;

public class FileMetrics {
    private int datafilesOnline;     // a1
    private int datafilesOffline;    // a2
    private long tamanoDatafiles;    // a3
    private long espacioTablespaces; // a4
    private int tempfiles;           // a5
    private int redoLogs;            // a6
    private int archivosInvalidos;   // a7
    private int archivosInaccesibles;

    public int getDatafilesOnline() {
        return datafilesOnline;
    }

    public void setDatafilesOnline(int datafilesOnline) {
        this.datafilesOnline = datafilesOnline;
    }

    public int getDatafilesOffline() {
        return datafilesOffline;
    }

    public void setDatafilesOffline(int datafilesOffline) {
        this.datafilesOffline = datafilesOffline;
    }

    public long getTamanoDatafiles() {
        return tamanoDatafiles;
    }

    public void setTamanoDatafiles(long tamanoDatafiles) {
        this.tamanoDatafiles = tamanoDatafiles;
    }

    public long getEspacioTablespaces() {
        return espacioTablespaces;
    }

    public void setEspacioTablespaces(long espacioTablespaces) {
        this.espacioTablespaces = espacioTablespaces;
    }

    public int getTempfiles() {
        return tempfiles;
    }

    public void setTempfiles(int tempfiles) {
        this.tempfiles = tempfiles;
    }

    public int getRedoLogs() {
        return redoLogs;
    }

    public void setRedoLogs(int redoLogs) {
        this.redoLogs = redoLogs;
    }

    public int getArchivosInvalidos() {
        return archivosInvalidos;
    }

    public void setArchivosInvalidos(int archivosInvalidos) {
        this.archivosInvalidos = archivosInvalidos;
    }

    public int getArchivosInaccesibles() {
        return archivosInaccesibles;
    }

    public void setArchivosInaccesibles(int archivosInaccesibles) {
        this.archivosInaccesibles = archivosInaccesibles;
    }
}
