package com.example.mediscanner_firebase.Sophia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mediscanner_firebase.Christiane.Calendar;
import com.example.mediscanner_firebase.R;
import com.example.mediscanner_firebase.Verena.Wound;
import com.example.mediscanner_firebase.Yara.Maps;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Medication extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout medication, maps, calendar,wound;

    DatabaseReference databaseReference;

    Button buttonPlus;
    ListView listView;
    ArrayList<MedicationItem> medicationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlus = findViewById(R.id.button_plus);
        listView = findViewById(R.id.medicationListView);
        medicationList = new ArrayList<>();

        drawerLayout=findViewById(R.id.drawerLayout);
        medication=findViewById(R.id.mediscanner);
        maps=findViewById(R.id.maps);
        menu=findViewById(R.id.menu);
        calendar=findViewById(R.id.calendar);
        wound=findViewById(R.id.camera);

        medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Medication.this, Maps.class);
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
               redirectActivity(Medication.this, Wound.class);
           }
       });
       calendar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               redirectActivity(Medication.this, Calendar.class);
           }
       });


        // Hier rufen Sie die Erinnerungszeiten aus den SharedPreferences ab und planen die Erinnerungen
        SharedPreferences preferences = getSharedPreferences("Erinnerungen", MODE_PRIVATE);
        String moText = preferences.getString("moText", "");
        String miText = preferences.getString("miText", "");
        String abText = preferences.getString("abText", "");
        String naText = preferences.getString("naText", "");

        // Hier sollten Sie die Methode zum Planen der Erinnerungen aufrufen
        scheduleReminders(moText, miText, abText, naText);



        databaseReference = FirebaseDatabase.getInstance().getReference("medication");

        // Hier sollten Sie einen ValueEventListener hinzufügen, um die Daten aus der Firebase-Datenbank abzurufen
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Löschen Sie die vorhandenen Daten in der Liste, um die Aktualisierung sicherzustellen
                medicationList.clear();

                // Durchlaufen Sie alle Kinder (Medikationseinträge) in der Firebase-Datenbank
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extrahieren Sie die Daten für jedes Medikationselement
                    String labelText = snapshot.child("labelText").getValue(String.class);
                    String titelText = snapshot.child("titelText").getValue(String.class);
                    //String dosageText = snapshot.child("dosageText").getValue(String.class);
                    String einnahmeZeiten = snapshot.child("einnahmeZeiten").getValue(String.class);
                    String itemId = snapshot.getKey(); // Die ID des Elements in der Firebase-Datenbank

                    // Fügen Sie das Medikationselement der Liste hinzu
                    MedicationItem medicationItem = new MedicationItem(labelText, titelText, einnahmeZeiten);
                    medicationList.add(medicationItem);
                }

                // Aktualisieren Sie Ihre ListView oder RecyclerView mit der aktualisierten Medikamentenliste
                MedicationAdapter adapter = new MedicationAdapter(Medication.this, medicationList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });

        buttonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Medication.this, MediScanner.class);
                startActivity(intent);
            }
        });

        // Hier setzen Sie einen OnItemClickListener für Ihre ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Hier können Sie den Löschvorgang durchführen
                deleteMedicationItem(position);
            }
        });
    }

    // Hier ist eine Methode, um ein Medikationselement zu löschen
    public void deleteMedicationItem(int position) {
        MedicationItem itemToDelete = medicationList.get(position);

        // Hier löschen Sie das Element aus der Liste
        medicationList.remove(position);

        // Aktualisieren Sie Ihre ListView oder RecyclerView mit der aktualisierten Medikamentenliste
        MedicationAdapter adapter = new MedicationAdapter(Medication.this, medicationList);
        listView.setAdapter(adapter);

        // Durchlaufen Sie alle Kinder (Medikationseinträge) in der Firebase-Datenbank
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extrahieren Sie die Daten für jedes Medikationselement
                    String labelText = snapshot.child("labelText").getValue(String.class);
                    String titelText = snapshot.child("titelText").getValue(String.class);
                    String dosageText = snapshot.child("dosageText").getValue(String.class);
                    String einnahmeZeiten = snapshot.child("einnahmeZeiten").getValue(String.class);

                    // Überprüfen , ob die Daten mit dem zu löschenden Element übereinstimmen
                    if (labelText.equals(itemToDelete.getLabel()) &&
                            titelText.equals(itemToDelete.getTitle()) &&
                            einnahmeZeiten.equals(itemToDelete.getUsage())) {

                        // Hier löschen Sie das Element aus der Firebase-Datenbank
                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Medication.this, "Medikation gelöscht", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Falls das Löschen in der Datenbank fehlschlägt, fügen Sie das Element wieder zur Liste hinzu
                                    medicationList.remove(itemToDelete); // Entfernen Sie das Element aus der Liste
                                    adapter.notifyDataSetChanged(); // Benachrichtigen Sie den Adapter über die Änderung
                                    Toast.makeText(Medication.this, "Fehler beim Löschen der Medikation", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break; // Brechen Sie die Schleife ab, da das Element gefunden wurde
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
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
    // Hier ist die Methode für den ImageButton-Klick
    public void onDeleteButtonClick(View view) {
        deleteMedicationItem(listView.getPositionForView(view));
    }

    private void scheduleReminders(String moText, String miText, String abText, String naText) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Benachrichtigungstexte
        String moNotificationText = "Nehmen Sie Ihr Medikament ein (Morgens)";
        String miNotificationText = "Nehmen Sie Ihr Medikament ein (Mittags)";
        String abNotificationText = "Nehmen Sie Ihr Medikament ein (Abends)";
        String naNotificationText = "Nehmen Sie Ihr Medikament ein (Nachts)";

        // Erinnerungszeiten in Millisekunden umwandeln (angenommen, die Zeiten sind im Format HH:mm)
        long moTime = convertTimeStringToMillis(moText);
        long miTime = convertTimeStringToMillis(miText);
        long abTime = convertTimeStringToMillis(abText);
        long naTime = convertTimeStringToMillis(naText);

        // Erstellen Sie Intents für die Benachrichtigungen
        Intent moIntent = new Intent(this, NotificationReceiver.class);
        moIntent.putExtra("notificationText", moNotificationText);
        PendingIntent moPendingIntent = PendingIntent.getBroadcast(this, 0, moIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent miIntent = new Intent(this, NotificationReceiver.class);
        miIntent.putExtra("notificationText", miNotificationText);
        PendingIntent miPendingIntent = PendingIntent.getBroadcast(this, 1, miIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent abIntent = new Intent(this, NotificationReceiver.class);
        abIntent.putExtra("notificationText", abNotificationText);
        PendingIntent abPendingIntent = PendingIntent.getBroadcast(this, 2, abIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent naIntent = new Intent(this, NotificationReceiver.class);
        naIntent.putExtra("notificationText", naNotificationText);
        PendingIntent naPendingIntent = PendingIntent.getBroadcast(this, 3, naIntent, PendingIntent.FLAG_IMMUTABLE);

        // Setzen Sie die Erinnerungen mit dem AlarmManager
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, moTime, AlarmManager.INTERVAL_DAY, moPendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, miTime, AlarmManager.INTERVAL_DAY, miPendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, abTime, AlarmManager.INTERVAL_DAY, abPendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, naTime, AlarmManager.INTERVAL_DAY, naPendingIntent);
        }
    }

    // Hilfsmethode zur Konvertierung der Uhrzeitzeichenfolge in Millisekunden
    private long convertTimeStringToMillis(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeString);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
