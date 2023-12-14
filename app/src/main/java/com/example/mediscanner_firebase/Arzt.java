package com.example.mediscanner_firebase;

import com.google.android.gms.maps.model.LatLng;

public class Arzt {
    private LatLng position;
    private String name;
    private String adresse;
    private String telefonnummer;
    private String oeffnungszeiten;

    // Konstruktor, Getter und Setter hier

    // Beispiel: Konstruktor
    public Arzt(LatLng position, String name, String adresse, String telefonnummer, String oeffnungszeiten) {
        this.position = position;
        this.name = name;
        this.adresse = adresse;
        this.telefonnummer = telefonnummer;
        this.oeffnungszeiten = oeffnungszeiten;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public String getOeffnungszeiten(){
        return oeffnungszeiten;
    }
}
