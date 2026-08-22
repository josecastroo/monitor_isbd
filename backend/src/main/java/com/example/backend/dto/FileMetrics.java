package com.example.backend.dto;

import lombok.Data;

public class FileMetrics {
    private Integer datafilesOnline;
    private Integer datafilesOffline;
    private Integer redoLogsNormales;
    private Integer redoLogsProblemas;

    public Integer getDatafilesOnline() {
        return datafilesOnline;
    }

    public void setDatafilesOnline(Integer datafilesOnline) {
        this.datafilesOnline = datafilesOnline;
    }

    public Integer getDatafilesOffline() {
        return datafilesOffline;
    }

    public void setDatafilesOffline(Integer datafilesOffline) {
        this.datafilesOffline = datafilesOffline;
    }

    public Integer getRedoLogsNormales() {
        return redoLogsNormales;
    }

    public void setRedoLogsNormales(Integer redoLogsNormales) {
        this.redoLogsNormales = redoLogsNormales;
    }

    public Integer getRedoLogsProblemas() {
        return redoLogsProblemas;
    }

    public void setRedoLogsProblemas(Integer redoLogsProblemas) {
        this.redoLogsProblemas = redoLogsProblemas;
    }
}
