package com.example.mediscanner_firebase.Verena;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mediscanner_firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Die Klasse "UpdateActivity" ermöglicht es Benutzern, vorhandene Datensätze zu bearbeiten und in der App zu aktualisieren.
 * Sie ist eng mit der Firebase-Datenbank und dem Storage verbunden, um die Aktualisierung von Daten und Bildern zu ermöglichen
 *
 * Funktionalitäten:
 * Auswahl und Anzeige von vorhandenen Bildern für die Aktualisierung.
 * Bearbeitung und Speicherung von aktualisierten Daten in der Firebase-Echtzeitdatenbank.
 * Löschen des alten Bildes aus Firebase Storage nach erfolgreicher Aktualisierung.
 */

public class UpdateActivity extends AppCompatActivity {

    /**
     * @updateImage: Ein ImageView-Element zur Anzeige des ausgewählten Bildes für die Aktualisierung.
     * @updateButton: Ein Button zur Auslösung des Aktualisierungsvorgangs.
     * @updateDesc: Ein EditText-Feld für die Bearbeitung von Beschreibungen.
     * @updateLang: Ein EditText-Feld für die Bearbeitung von zusätzlichen Details oder der Sprache.
     * @updateTitle: Ein Spinner zur Auswahl des aktualisierten Themas.
     * @title, @desc, @lang: Zeichenfolgen zur Zwischenspeicherung von Daten für die Aktualisierung.
     * @imageUrl: Eine Zeichenfolge, die die URL des aktualisierten Bildes enthält.
     * @key, @oldImageURL: Zeichenfolgen, die den Schlüssel und die alte Bild-URL des zu aktualisierenden Datensatzes speichern.
     * @uri: Ein Uri-Objekt, das auf das ausgewählte Bild für die Aktualisierung verweist.
     * @databaseReference: Referenzobjekt für die Firebase-Datenbank.
     * @storageReference: Referenzobjekt für Firebase Storage.
     *
     */


    ImageView updateImage;
    Button updateButton;
    EditText updateDesc,  updateLang;
    Spinner updateTitle;
    String title, desc, lang;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    /**
     * onCreate(Bundle savedInstanceState) - Methode
     * Initialisiert die UI-Elemente und verknüpft sie mit den entsprechenden Ansichten.
     * Definiert einen Dropdown-Adapter für den Spinner zur Auswahl von Hauterkrankungen.
     * Implementiert die Bildauswahl über die Galerie des Geräts.
     * Extrahiert Daten des zu aktualisierenden Datensatzes und zeigt sie in den entsprechenden UI-Elementen an
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updateDesc = findViewById(R.id.updateDesc);
        updateImage = findViewById(R.id.updateImage);
        updateLang = findViewById(R.id.updateLang);
        updateTitle = findViewById(R.id.uploadTopic);

        // Daten für das Dropdown-Menü
        String[] hauterkrankungen = {"Akne", "Neurodermitis", "Schuppenflechte", "Muttermal", "Rosazea", "Hautkrebs", "Ekzeme", "Warzen","Nesselsucht", "Hautpilzinfektionen", "Scapies",  "Andere"};
        // Adapter für den Spinner erstellen
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hauterkrankungen);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Adapter an den Spinner binden
        updateTitle.setAdapter(spinnerAdapter);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setSelection(spinnerAdapter.getPosition(bundle.getString("Title")));
            updateDesc.setText(bundle.getString("Description"));
            updateLang.setText(bundle.getString("Language"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }
                                                                                //Wound
        databaseReference = FirebaseDatabase.getInstance().getReference("Wound").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(UpdateActivity.this, Wound.class);
                startActivity(intent);
            }
        });
    }

    /**
     * saveData()-methode
     * Speichert das ausgewählte Bild in Firebase Storage.
     * Zeigt einen Fortschrittsdialog während des Speichervorgangs an.
     * Speichert die URL des aktualisierten Bildes und ruft updateData() auf.
     */

    public void saveData(){
        //DA IS IRG A FEHLERMELDUNG

        storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });

    }

    /**
     * updateData()-methode
     * Extrahiert die aktualisierten Daten wie Titel, Beschreibung, Sprache und Bild-URL.
     * Aktualisiert den Datensatz in der Firebase-Echtzeitdatenbank.
     * Löscht das alte Bild aus Firebase Storage.
     * Zeigt eine Erfolgsmeldung an und beendet die Aktivität nach erfolgreicher Aktualisierung.
     */
    public void updateData(){
        title = updateTitle.getSelectedItem().toString().trim();
        desc = updateDesc.getText().toString().trim();
        lang = updateLang.getText().toString();

        DataClass dataClass = new DataClass(title, desc, lang, imageUrl);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                    reference.delete();
                    Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}