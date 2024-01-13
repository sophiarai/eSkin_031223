package com.example.mediscanner_firebase.Verena;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mediscanner_firebase.Christiane.Calendar;
import com.example.mediscanner_firebase.R;
import com.example.mediscanner_firebase.Sophia.Medication;
import com.example.mediscanner_firebase.Yara.Maps;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse "Wound" ist eine Aktivität in einer Android-Anwendung, die Funktionalitäten für die Verwaltung von Wunddaten bereitstellt.
 * Sie enthält Methoden und Implementierungen für die Benutzeroberfläche sowie die Interaktion mit einer Firebase-Datenbank.
 */

public class Wound extends AppCompatActivity {

    /**
     * @fab : Ein Floating Action Button (FAB), der eine Aktion zum Hochladen von Wunddaten auslöst.
     * @databaseReference : Referenzobjekt für die Firebase-Datenbank.
     * @eventListener : Event-Listener, der Änderungen in der Datenbank überwacht.
     * @recyclerView : Eine RecyclerView zur Anzeige von Wunddaten.
     * @dataList : Eine Liste von Objekten des Typs DataClass, die die Wunddaten enthält.
     * @adapter : Ein benutzerdefinierter Adapter (MyAdapter) für die RecyclerView.
     * @searchView : Ein Suchfeld für die Filterung von Wunddaten.
     *
     * Weitere Attribute repräsentieren verschiedene UI-Elemente wie DrawerLayout, ImageView, und LinearLayout für Menüoptionen.
     */
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    SearchView searchView;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout medication, maps, calendar,wound;

    /**
     * Die onCreate Methode initialisiert die UI-Komponenten.
     * Setzt Layout-Manager für die RecyclerView und definiert einen Dialog für den Ladevorgang.
     * Verknüpft die Datenbankreferenz mit dem entsprechenden Firebase-Pfad.
     * Implementiert einen Event-Listener, um Daten aus der Datenbank abzurufen und die RecyclerView zu aktualisieren.
     * Konfiguriert die Suchfunktionalität und definiert Ereignisse für verschiedene UI-Elemente.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wound_docu);

        drawerLayout=findViewById(R.id.drawerLayout);
        medication=findViewById(R.id.mediscanner);
        maps=findViewById(R.id.maps);
        menu=findViewById(R.id.menu);
        calendar=findViewById(R.id.calendar);
        wound=findViewById(R.id.camera);
        recyclerView = findViewById(R.id.recyclerView);

        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Wound.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Wound.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new MyAdapter(Wound.this, dataList);
        recyclerView.setAdapter(adapter);
                                                                                //Weg zur Firebaseverknüpfung
        databaseReference = FirebaseDatabase.getInstance().getReference("Wound");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);

                    dataClass.setKey(itemSnapshot.getKey());

                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Wound.this, UploadActivity.class);
                startActivity(intent);
            }
        });




        medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Wound.this, Medication.class);
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Wound.this, Maps.class);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        wound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Wound.this, Calendar.class);
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





    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if (dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }
}