package com.example.mediscanner_firebase.Verena;


/**
 * Die Klasse "DataClass" repräsentiert Datenobjekte, die Informationen über Hauterkrankungen in einer Android-Anwendung speichern.
 * Diese Klasse enthält Attribute und Methoden zum Abrufen und Festlegen von Datenfeldern.
 */

public class DataClass {

    /**
     * Attribute:
     * @dataTitle: Eine Zeichenfolge, die den Titel der Hauterkrankung speichert.
     * @dataDesc: Eine Zeichenfolge, die die Beschreibung der Hauterkrankung speichert.
     * @dataLang: Eine Zeichenfolge, die die Sprache oder zusätzliche Details der Hauterkrankung speichert.
     * @dataImage: Eine Zeichenfolge, die die URL des Bildes der Hauterkrankung speichert.
     * @key: Eine Zeichenfolge zur Identifizierung des Datenobjekts in der Datenbank.
     */

    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
    private String key;

    public String getKey() {
        return key;
    }


    /**
     * Getter und Setter
     * getKey(), setKey(String key): Methoden zum Abrufen und Festlegen des Schlüssels key.
     * getDataTitle(): Methode zum Abrufen des Titels dataTitle.
     * getDataDesc(): Methode zum Abrufen der Beschreibung dataDesc.
     * getDataLang(): Methode zum Abrufen der Sprache oder zusätzlicher Details dataLang.
     * getDataImage(): Methode zum Abrufen der Bild-URL dataImage.
     */

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public String getDataImage() {
        return dataImage;
    }

    /**
     * Konstruktoren
     * DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage):
     * Ein Konstruktor, der die Datenobjekte initialisiert und ihnen Werte für Titel, Beschreibung, Sprache und Bild-URL zuweist.
     * DataClass(): Ein leerer Konstruktor.
     */
    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
    }
    public DataClass(){

    }

}
