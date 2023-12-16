package com.example.mediscanner_firebase.Christiane;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mediscanner_firebase.R;
import com.example.mediscanner_firebase.Sophia.Medication;
import com.example.mediscanner_firebase.Verena.Wound;
import com.example.mediscanner_firebase.Yara.Maps;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
public class Calendar extends AppCompatActivity implements CalAdapter.OnDeleteItemClickListener{
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout medication, maps, calendar,wound;

    private CalendarView calendarView; // Kalenderansicht zur Auswahl von Datumsangaben
    private long selectedDateMillis; // Variable zur Speicherung des ausgewählten Datums in Millisekunden

    private RecyclerView eventAnzeige; // RecyclerView zur Anzeige von Ereignisdaten
    private ArrayList<DataClass> dataList; // Liste zur Speicherung von Daten
    private CalAdapter calAdapter; // Adapter für die RecyclerView
    private DatabaseReference selectedDateRef; // Referenz für die Firebase-Datenbank

    private long selectedTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        drawerLayout=findViewById(R.id.drawerLayout);
        medication=findViewById(R.id.mediscanner);
        maps=findViewById(R.id.maps);
        menu=findViewById(R.id.menu);
        calendar=findViewById(R.id.calendar);
        wound=findViewById(R.id.camera);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Calendar.this, Medication.class);
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Calendar.this, Maps.class);
            }
        });
        wound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Calendar.this, Wound.class);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Calendar.this, Calendar.class);
            }
        });



        // Initialisierung von UI-Elementen
        FloatingActionButton fabButton = findViewById(R.id.fab);
        eventAnzeige = findViewById(R.id.eventAnzeige);

        // Initialisierung der Datenliste und des Adapters für die RecyclerView
        dataList = new ArrayList<>();
        calAdapter = new CalAdapter(dataList);
        calAdapter.setOnDeleteItemClickListener(this);
        eventAnzeige.setAdapter(calAdapter);

        // LayoutManager für die RecyclerView festlegen
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        eventAnzeige.setLayoutManager(layoutManager);

        // Initialisierung des CalendarView und Festlegung des aktuellen Datums
        calendarView = findViewById(R.id.calendarView);
        android.icu.util.Calendar today = android.icu.util.Calendar.getInstance();
        today.set(android.icu.util.Calendar.HOUR_OF_DAY, 0);
        today.set(android.icu.util.Calendar.MINUTE, 0);
        today.set(android.icu.util.Calendar.SECOND, 0);
        today.set(android.icu.util.Calendar.MILLISECOND, 0);

        selectedDateMillis = today.getTimeInMillis();


        selectedTimeMillis = getIntent().getLongExtra("Datum1", 0);

        // Listener für die Auswahl eines Datums im CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Hier wird das Datum aus dem KalenderView gesetzt
                today.set(year, month, dayOfMonth);
                selectedDateMillis = today.getTimeInMillis();
                getEventsForSelectedDate(selectedDateMillis);
            }
        });

        // OnClickListener für den FloatingActionButton zum Hinzufügen von Ereignissen
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDateMillis > 0) {
                    Intent myIntent = new Intent(getApplicationContext(), UploadActivity.class);
                    myIntent.putExtra("Datum", selectedDateMillis);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(Calendar.this, "Bitte wähle ein Datum aus.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer (DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent= new Intent (activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }




    // Methode zum Abrufen von Ereignisdaten für das ausgewählte Datum aus der Firebase-Datenbank
    private void getEventsForSelectedDate(long selectedTime) {
        // Aktuelle Datenliste löschen, um Platz für neue Daten zu machen
        dataList.clear();

        // Referenz zur Firebase-Datenbank für die Abfrage nach dem ausgewählten Datum
        selectedDateRef = FirebaseDatabase.getInstance().getReference("Calendar");


        Query query = selectedDateRef.orderByChild("timestampMillis").equalTo(selectedDateMillis);
        // Query query = selectedDateRef.orderByChild("selectedTimeMillis").equalTo(selectedDateMillis);

        // ValueEventListener, um auf Datenänderungen in der Query zu reagieren
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Datenliste leeren, bevor neue Daten hinzugefügt werden
                dataList.clear();

                // Iteriere durch alle Snapshots, um Daten zu erhalten und sie zur Liste hinzuzufügen
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }

                // Log-Ausgabe der Anzahl der erhaltenen Daten
                Log.d("FirebaseData", "Anzahl der erhaltenen Daten: " + dataList.size());

                // RecyclerView aktualisieren, um die neuen Daten anzuzeigen
                calAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Anzeige einer Fehlermeldung, falls Daten nicht abgerufen werden können
                Toast.makeText(Calendar.this, "Fehler beim Abrufen der Daten", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteItemClick(DataClass dataItem) {
        // Hier wird der Löschvorgang in der Firebase-Datenbank durchgeführt
        //String key = String.valueOf(dataItem.getTimestampMillis());
        String key = String.valueOf(dataItem.getSelectedTimeMillis());
        FirebaseDatabase.getInstance().getReference("Calendar").child(key)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Erfolgreich gelöscht
                            Toast.makeText(Calendar.this, "Eintrag gelöscht!", Toast.LENGTH_SHORT).show();
                            // Weitere Aktionen nach dem Löschen
                        } else {
                            // Fehler beim Löschen
                            Toast.makeText(Calendar.this, "Fehler beim Löschen!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}