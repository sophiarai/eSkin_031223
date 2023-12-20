package com.example.mediscanner_firebase.Christiane;

import com.google.firebase.Timestamp;

public class DataClass {


    private String uploadEvent;
    // private Timestamp timestamp;
    private long timestampMillis;

    public long getSelectedTimeMillis() {
        return selectedTimeMillis;
    }

    public void setSelectedTimeMillis(long selectedTimeMillis) {
        this.selectedTimeMillis = selectedTimeMillis;
    }

    long selectedTimeMillis;

    public DataClass(
            long timestampMillis,long selectedTimeMillis
            //Timestamp timestamp
            ,String uploadEvent) {
        this.uploadEvent= uploadEvent;
        this.selectedTimeMillis= selectedTimeMillis;
        this.timestampMillis=timestampMillis;
    }
    public DataClass() {
        // Leerer Konstruktor oder Initialisierung von Variablen, falls erforderlich
    }


    public String getUploadEvent() {
        return uploadEvent;
    }


    public long getTimestampMillis() {
        return timestampMillis;
    }




}

