package com.example.mediscanner_firebase.Yara;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mediscanner_firebase.Christiane.Calendar;
import com.example.mediscanner_firebase.Sophia.Medication;
import com.example.mediscanner_firebase.R;
import com.example.mediscanner_firebase.Verena.Wound;

public class Maps extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout medication, maps, calendar, wound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        drawerLayout = findViewById(R.id.drawerLayout);
        medication = findViewById(R.id.mediscanner);
        maps = findViewById(R.id.maps);
        menu = findViewById(R.id.menu);
        calendar = findViewById(R.id.calendar);
        wound = findViewById(R.id.camera);

        medication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Maps.this, Medication.class);
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hier können Sie die MapsActivity öffnen
                Intent intent = new Intent(Maps.this, MapsActivity.class);
                startActivity(intent);
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
                redirectActivity(Maps.this, Wound.class);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(Maps.this, Calendar.class);
            }
        });
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(AppCompatActivity activity, Class<?> secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
