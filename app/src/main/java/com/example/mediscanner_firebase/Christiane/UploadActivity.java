package com.example.mediscanner_firebase.Christiane;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.mediscanner_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadActivity extends AppCompatActivity {

    Button saveButton;
    EditText uploadEvent;
    Timestamp timestamp;
    long timestampMillis;

    long selectedTimeMillis;

    TimePicker timePicker;
    Switch daySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_calendar);

        timePicker = findViewById(R.id.timePicker);
        daySwitch = findViewById(R.id.daySwitch);

        // DATUM AUS KALENDER
        // long
        timestampMillis = getIntent().getLongExtra("Datum", 0); // 0 ist der Standardwert

        // Konvertiere den Long in ein Date-Objekt
        Date date = new Date(timestampMillis);
        System.out.println("date"+date);
        // Erzeuge ein Firebase Timestamp-Objekt mit den Millisekunden aus dem Date-Objekt
        timestamp = new Timestamp(date);
        System.out.println("timestamp"+timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); // Das gewünschte Datumsformat
        String formattedDate = dateFormat.format(date);

        TextView datumAnzeige = findViewById(R.id.datumAnzeige);
        datumAnzeige.setText(formattedDate); // Zeige das formatierte Datum im TextView an

        daySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Überprüfe den Status des Switch
                if (isChecked) {
                    // Wenn der Switch aktiviert ist, deaktiviere den TimePicker
                    timePicker.setEnabled(false);
                } else {
                    // Wenn der Switch deaktiviert ist, aktiviere den TimePicker
                    timePicker.setEnabled(true);
                }
            }
        });

        saveButton = findViewById(R.id.saveButton);
        uploadEvent = findViewById(R.id.uploadEvent);

        // Speichern-Button-Klick-Event
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSwitchChecked = daySwitch.isChecked();

                // Wenn der Switch nicht aktiviert ist
                if (!isSwitchChecked) {
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();

                    // Erstelle ein Calendar-Objekt und setze die Stunden und Minuten
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);

                    // Holen der Zeit in Millisekunden
                    selectedTimeMillis = calendar.getTimeInMillis();
                } else {
                    // Setze die Zeit auf 0 oder einen Standardwert, wenn der Switch aktiviert ist
                    selectedTimeMillis = 0; // oder anderen passenden Standardwert
                }

                Intent myIntent = new Intent(getApplicationContext(), com.example.mediscanner_firebase.Christiane.Calendar.class);
                myIntent.putExtra("Datum1", selectedTimeMillis);
                startActivity(myIntent);
                uploadData();
            }
        });
    }


    public void uploadData() {
        String event = uploadEvent.getText().toString();

        // Überprüfe, ob die Zeit gespeichert werden soll (wenn der Switch aktiviert ist)
        if (selectedTimeMillis != 0) {
            DataClass dataClass = new DataClass(timestampMillis, selectedTimeMillis, event);
            //String key = String.valueOf(timestampMillis);
            String key = String.valueOf(selectedTimeMillis);
            FirebaseDatabase.getInstance().getReference("Calendar").child(key)
                    .setValue(dataClass)
                    .addOnCompleteListener(task -> Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            DataClass dataClass = new DataClass(timestampMillis, 0, event);
            //String key = String.valueOf(timestampMillis);
            String key = String.valueOf(selectedTimeMillis);

            FirebaseDatabase.getInstance().getReference("Calendar").child(key)
                    .setValue(dataClass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UploadActivity.this, "Ganztägiges Event gespeichert", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }}
