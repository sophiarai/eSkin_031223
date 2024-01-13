package com.example.mediscanner_firebase.Verena;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.util.Calendar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 *
 * Diese Klasse ist verantwortlich für das Hochladen von Bildern und assoziierten Informationen in Firebase.
 * Sie handhabt die Interaktion mit der Benutzeroberfläche und ermöglicht es Benutzern, relevante Daten in der App zu speichern.
 *
 * Funktionalitäten:
 * Auswahl und Anzeige von Bildern aus der Galerie.
 * Speichern von Bildern und dazugehörigen Details in Firebase Storage und Realtime Database.
 * Verwendung eines Spinners für die Auswahl von Hauterkrankungen.
 *
 */

public class UploadActivity extends AppCompatActivity {
    /**
     * @databaseReference: Referenzobjekt für die Firebase-Datenbank.
     * @uploadImage: Ein ImageView-Element zur Anzeige des ausgewählten Bildes.
     * @uploadDesc: Ein EditText-Feld für die Eingabe von Beschreibungen.
     * @uploadTopic: Ein Spinner zur Auswahl des Themas für das hochgeladene Bild.
     * @uploadLang: Ein EditText-Feld für die Eingabe von zusätzlichen Details oder der Sprache.
     * @saveButton: Ein Button zur Auslösung des Speichervorgangs.
     * @imageURL: Eine Zeichenfolge, die die URL des hochgeladenen Bildes enthält.
     * @uri: Ein Uri-Objekt, das auf das ausgewählte Bild verweist.
     *
     */

    DatabaseReference databaseReference;
    ImageView uploadImage;
    Button saveButton;
    EditText uploadDesc, uploadLang;
    Spinner uploadTopic;
    String imageURL;
    Uri uri;


    /**
     * onCreate(Bundle savedInstanceState)
     * Initialisiert die UI-Elemente und verknüpft sie mit den entsprechenden Ansichten.
     * Definiert einen Dropdown-Adapter für den Spinner zur Auswahl von Hauterkrankungen.
     * Implementiert die Bildauswahl über die Galerie des Geräts.
     * Definiert den OnClickListener für den Speicherbutton.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDesc);
        uploadTopic = findViewById(R.id.uploadTopic);
        uploadLang = findViewById(R.id.uploadLang);
        saveButton = findViewById(R.id.saveButton);
                                                                                // war vorher Wound
        databaseReference=FirebaseDatabase.getInstance().getReference("Wound");

        // Daten für das Dropdown-Menü
        String[] hauterkrankungen = {"Akne", "Neurodermitis", "Schuppenflechte", "Muttermal", "Rosazea", "Hautkrebs", "Ekzeme", "Warzen","Nesselsucht", "Hautpilzinfektionen", "Scapies",  "Andere"};

        // Adapter für den Spinner erstellen
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hauterkrankungen);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Adapter an den Spinner binden
        uploadTopic.setAdapter(spinnerAdapter);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }


    /**
     * saveData()-Methode
     * Speichert das ausgewählte Bild in Firebase Storage.
     * Zeigt einen Fortschrittsdialog während des Speichervorgangs an.
     * Speichert die URL des hochgeladenen Bildes und ruft uploadData() auf.
     */

    public void saveData(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
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
                imageURL = urlImage.toString();
                uploadData();
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
     * uploadData()-Methode
     * Extrahiert die ausgewählten Daten wie Titel, Beschreibung, Sprache und Bild-URL.
     * Erstellt ein Objekt vom Typ DataClass mit den gesammelten Daten.
     * Speichert die Daten in der Firebase-Echtzeitdatenbank mit einem eindeutigen Zeitstempel als Schlüssel.
     */

    public void uploadData(){
        String title = uploadTopic.getSelectedItem().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();

        DataClass dataClass = new DataClass(title, desc, lang, imageURL);

        // Hier die Daten in die Realtime Database speichern
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        currentDate = currentDate.replace(".", "-");

        databaseReference.child(currentDate)
                .setValue(dataClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}