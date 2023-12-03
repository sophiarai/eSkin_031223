package com.example.mediscanner_firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicationAdapter extends ArrayAdapter<MedicationItem> {
    public MedicationAdapter(Context context, ArrayList<MedicationItem> medicationList) {
        super(context, 0, medicationList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MedicationItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        TextView labelTextView = convertView.findViewById(R.id.labelTextView);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView dosageTextView = convertView.findViewById(R.id.dosageTextView);
        TextView usageTextView = convertView.findViewById(R.id.usageTextView);

        labelTextView.setText("Label: " + item.getLabel());
        titleTextView.setText("Title: " + item.getTitle());
        dosageTextView.setText("Dosierung: " + item.getDosage());
        usageTextView.setText("Einnahme: " + item.getUsage());

        return convertView;
    }
}