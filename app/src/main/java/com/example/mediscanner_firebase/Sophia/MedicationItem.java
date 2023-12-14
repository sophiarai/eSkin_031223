package com.example.mediscanner_firebase.Sophia;

public class MedicationItem {


    private String label;
    private String title;
    private String dosage;
    private String usage;

    public MedicationItem(String label, String title, String dosage, String usage) {

        this.label = label;
        this.title = title;
        this.dosage = dosage;
        this.usage = usage;
    }

    public String getLabel() {
        return label;
    }

    public String getTitle() {
        return title;
    }

    public String getDosage() {
        return dosage;
    }

    public String getUsage() {
        return usage;
    }


}

