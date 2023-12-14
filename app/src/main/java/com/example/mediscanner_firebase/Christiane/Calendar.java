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

public class Calendar extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout medication, maps, calendar,wound;

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
}