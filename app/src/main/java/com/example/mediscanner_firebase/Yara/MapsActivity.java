package com.example.mediscanner_firebase.Yara;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.mediscanner_firebase.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    FrameLayout map;
    ListView markerListView;
    List<MarkerOptions> markerOptionsList;

    private List<Arzt> arztListe = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.map);
        markerListView = findViewById(R.id.markerListView);

        markerOptionsList = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        populateArztList();
    }

    private void populateArztList() {
        arztListe.add(new Arzt(new LatLng(
                47.067176, 15.444035),
                "Dr. Ralf Ludwig",
                "Reitschulgasse 1, 8010 Graz",
                "0316 327920",
                "Mo: 07:30-12:00\n" +
                        "Di: 07:30-12:00\n" +
                        "Mi: 07:30-12:00, 13:00-16:00\n" +
                        "Do: 07:30-12:00, 13:00-17:00\n" +
                        "Fr: 07:30 - 12:00"));

        arztListe.add(new Arzt(new LatLng(
                47.0639742, 15.431564),
                "Dr. Nicola Ravnik-Ehrengruber",
                "Volksgartenstraße 26, 8020 Graz",
                "0650 2203331",
                "Mo, Di, Mi, Fr 09:00-13:00"));

        arztListe.add(new Arzt(new LatLng(47.060262, 15.426857),
                "Dr Christian Jantschitsch",
                "Lazarettgürtel 55/Stiege 2, 8020 Graz",
                "0316 771803",
                "M0: 13:00-17:00\n" +
                        "Di. 08:00-12:00\n" +
                        "Mi: 08:00-12:00\n" +
                        "Do: 13:00-17:00\n" +
                        "Fr: 08:00-12:00"));

        arztListe.add(new Arzt(new LatLng(47.07085, 15.43921),
                "Dr. Gerhard Leitinger",
                "Herrengasse 28, 8010 Graz",
                "0316 319434",
                "Mo: 8-16 Uhr\n" +
                        "Di: 14-20 Uhr\n" +
                        "Mi: 8-16 Uhr\n" +
                        "Do: 8-13, 14.30-18 Uhr\n" +
                        "Fr: 8-14 Uhr"));

        arztListe.add(new Arzt(new LatLng(47.066839, 15.46818),
                "Dr. Renate Schöllnast", "Waltendorfer Hauptstraße 95, 8010 Graz\n",
                "0316 228103",
                "Mo und Di 12:00-17:00\n" +
                        "Mi und Do 9:00-13:00\n" +
                        "Fr 9:00-11:00"));
    }

    private void populateMarkerList() {
        for (Arzt arzt : arztListe) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(arzt.getPosition())
                    .title(arzt.getName())
                    .snippet(arzt.getAdresse() + "\n" + "\n" +
                            "Telefon: " + arzt.getTelefonnummer() + "\n" + "\n" +
                            "Öffnungszeiten:\n" + arzt.getOeffnungszeiten());

            markerOptionsList.add(markerOptions);
        }
    }


    private void setupMarkerListView() {
        MarkerAdapter markerAdapter = new MarkerAdapter(this, markerOptionsList);
        markerListView.setAdapter(markerAdapter);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;

        populateMarkerList();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Arzt arzt : arztListe) {
            LatLng position = arzt.getPosition();
            builder.include(position);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(arzt.getName())
                    .snippet(arzt.getAdresse() + "\n" + arzt.getTelefonnummer());

            this.gMap.addMarker(markerOptions);
        }

        LatLngBounds bounds = builder.build();
        int padding = 100; // optional, um etwas Platz um die Marken zu lassen
        this.gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        // Setze den InfoWindowAdapter
        MarkerAdapter markerAdapter = new MarkerAdapter(this, markerOptionsList);
        gMap.setInfoWindowAdapter(markerAdapter);

        // Verknüpfe die Liste mit dem Adapter
        setupMarkerListView();
    }
}

