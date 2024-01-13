package com.example.mediscanner_firebase.Verena;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mediscanner_firebase.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Die Klasse "DetailActivity" dient dazu, detaillierte Informationen über einen ausgewählten Datensatz anzuzeigen
 * und dem Benutzer Optionen zum Bearbeiten oder Löschen des Datensatzes anzubieten.
 * Sie interagiert eng mit Firebase, um Daten und Bilder zu verwalten.
 */

public class DetailActivity extends AppCompatActivity {

    /**
     * Attribute:
     * @detailDesc, @detailTitle, @detailLang: TextView-Elemente zur Anzeige der Beschreibung, des Titels und der Sprache des Datensatzes.
     * @detailImage: ImageView-Element zur Anzeige des Bildes des Datensatzes.
     * @deleteButton, @editButton: FloatingActionButton-Elemente zur Auslösung von Lösch- und Bearbeitungsvorgängen.
     * @key: Eine Zeichenfolge zur Speicherung des Schlüssels des Datensatzes.
     * @imageUrl: Eine Zeichenfolge zur Speicherung der URL des Bildes des Datensatzes.
     */
    TextView detailDesc, detailTitle, detailLang;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";

    /**
     * onCreate(Bundle savedInstanceState)-Methode:
     * Initialisiert die UI-Elemente und zeigt die Details des übergebenen Datensatzes an, falls vorhanden.
     * Definiert OnClickListener für die Lösch- und Bearbeitungsschaltflächen.
     *
     * Funktionalitäten:
     * Anzeige der detaillierten Informationen eines Datensatzes, einschließlich Bild, Titel, Beschreibung und Sprache.
     * Möglichkeit zum Löschen des Datensatzes und des zugehörigen Bildes.
     * Möglichkeit zur Bearbeitung des Datensatzes durch Öffnen der "UpdateActivity".
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailLang = findViewById(R.id.detailLang);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Language"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

        /**
         * deleteButton.setOnClickListener()-Methode:
         * Löscht den Datensatz aus der Firebase-Datenbank und das zugehörige Bild aus dem Firebase Storage.
         * Zeigt eine Erfolgsmeldung an und kehrt zur vorherigen Aktivität zurück.
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                                                            //Wound
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Wound");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Wound.class));
                        finish();
                    }
                });
            }
        });

        /**
         * editButton.setOnClickListener()-Methode
         * Öffnet die Aktivität "UpdateActivity", um den ausgewählten Datensatz zu bearbeiten.
         */
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Language", detailLang.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }
}