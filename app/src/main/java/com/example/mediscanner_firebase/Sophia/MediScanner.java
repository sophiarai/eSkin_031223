package com.example.mediscanner_firebase.Sophia;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import okhttp3.Response;

import com.example.mediscanner_firebase.R;
import com.example.mediscanner_firebase.Verena.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Callback;



public class MediScanner extends AppCompatActivity {
    DatabaseReference databaseReference;
    private MaterialButton cameraBtn, galleryBtn, scanBtn, btn_back ;
    private ImageView imageIv;
    private EditText resultLabel, resultTitel, resultDosage, einnahme_morgens,einnahme_mittags, einnahme_abends,einnahme_nachts;


    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=101;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri imageUri=null;
    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanner barcodeScanner;
    private static final String TAG="MAIN_TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medi_scanner);

        cameraBtn= findViewById(R.id.camera_Btn);
       galleryBtn=findViewById(R.id.gallery_Btn);
        imageIv=findViewById(R.id.imageIV);
        scanBtn=findViewById(R.id.scanBtn);
        resultLabel=findViewById(R.id.label_Tv);
        resultTitel=findViewById(R.id.titel_Tv);
        resultDosage=findViewById(R.id.dosierung_Tv);
        einnahme_morgens=findViewById(R.id.moEditText);
        einnahme_mittags=findViewById(R.id.miEditText);
        einnahme_abends=findViewById(R.id.abEditText);
        einnahme_nachts=findViewById(R.id.naEditText);
        Button hinzufuegenButton = findViewById(R.id.button_hinzufügen);
        btn_back= findViewById(R.id.backButton);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        barcodeScannerOptions= new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        //barcodeScannerOptions = new BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();
        barcodeScanner= BarcodeScanning.getClient(barcodeScannerOptions);

        databaseReference= FirebaseDatabase.getInstance().getReference("medication");


       /* if (!checkStoragePermission()) {
            requestStoragePermission();
        }
*/
        hinzufuegenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labelText= resultLabel.getText().toString();
                String titelText= resultTitel.getText().toString();
                String dosageText= resultDosage.getText().toString();
                String moText=einnahme_morgens.getText().toString();
                String miText=einnahme_mittags.getText().toString();
                String abText=einnahme_abends.getText().toString();
                String naText=einnahme_nachts.getText().toString();

                // Überprüfen, ob Label, Titel und mindestens eine Einnahmezeit ausgefüllt sind
                if (labelText.isEmpty() || titelText.isEmpty() || (moText.isEmpty() && miText.isEmpty() && abText.isEmpty() && naText.isEmpty())) {
                    // Zeige eine Warnung an
                    Toast.makeText(MediScanner.this, getString(R.string.fill_out_field), Toast.LENGTH_SHORT).show();
                    return; // Beende die Methode hier, da die Bedingung nicht erfüllt ist
                }

                // Überprüfe und setze automatisch "0", wenn ein Textfeld leer ist
                if (moText.isEmpty()) {
                    moText = "0";
                }
                if (miText.isEmpty()) {
                    miText = "0";
                }
                if (abText.isEmpty()) {
                    abText = "0";
                }
                if (naText.isEmpty()) {
                    naText = "0";
                }

                //gewünschtes Format erstellen
                String einnahmeZeiten=moText + "," + miText + "," + abText + "," + naText;

                // Erinnerungszeiten in SharedPreferences speichern
                scheduleRemindersInMainActivity(moText, miText, abText, naText);

                saveMedicationToFirebase(labelText, titelText, dosageText, einnahmeZeiten);

                Intent intent= new Intent(MediScanner.this, Medication.class);
                intent.putExtra("labelText", labelText);
                intent.putExtra("titelText", titelText);
                intent.putExtra("dosageText", dosageText);
                intent.putExtra("einnahmeZeiten", einnahmeZeiten);
                startActivity(intent);
            }
        });




        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCameraPermission()){
                    pickImageCamera();
                }else{
                    requestCameraPermission();
                }
            }
        });

       /* galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkStoragePermission()){
                    pickImageGallery();
                }else{
                    requestStoragePermission();
                }
            }
        });
*/
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri==null){
                    Toast.makeText(MediScanner.this, getString(R.string.pick_image_first), Toast.LENGTH_SHORT).show();
                }else{
                    detectResultFromImage();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediScanner.this, Medication.class);
                startActivity(intent);
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            imageIv.setImageURI(imageUri);
                        } else {
                            Toast.makeText(MediScanner.this, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType(("image/*"));
                activityResultLauncher.launch(photoPicker);
            }
        });
    }

    private void saveMedicationToFirebase(String labelText, String titelText, String dosageText, String einnahmeZeiten) {
        // Generieren Sie einen eindeutigen Schlüssel für die Medikation
        String medicationId = databaseReference.push().getKey();

        // Erstellen Sie eine HashMap, um die Medikationsdaten zu speichern
        HashMap<String, Object> medicationMap = new HashMap<>();
        medicationMap.put("labelText", labelText);
        medicationMap.put("titelText", titelText);
        medicationMap.put("dosageText", dosageText);
        medicationMap.put("einnahmeZeiten", einnahmeZeiten);

        // Speichern Sie die Medikationsdaten in Firebase unter Verwendung des eindeutigen Schlüssels
        databaseReference.child(medicationId).setValue(medicationMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MediScanner.this, getString(R.string.medication_saved_successfully), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MediScanner.this, getString(R.string.error_saving_medication), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void scheduleRemindersInMainActivity(String moText, String miText, String abText, String naText) {
        SharedPreferences preferences = getSharedPreferences("Erinnerungen", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("moText", moText);
        editor.putString("miText", miText);
        editor.putString("abText", abText);
        editor.putString("naText", naText);
        editor.apply();
    }




    private void detectResultFromImage() {
        try {

                InputImage inputImage = InputImage.fromFilePath(this, imageUri);
                Task<List<Barcode>> barcodeResult = barcodeScanner.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        extractBarCodeQRCodeInfo(barcodes);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MediScanner.this, "Failed scanning due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        } catch (Exception e) {
            Toast.makeText(MediScanner.this, "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*private void pickImageGallery() {
        if(checkStoragePermission()) {
            Log.d(TAG, "pickImageGallery: Trying to pick image from gallery");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryActivityResultLauncher.launch(intent);
        }else{
            requestStoragePermission();
        }
    }
    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri: " + imageUri);
                        imageIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(MediScanner.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            */


    private void pickImageCamera(){
        ContentValues contentValues= new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");

        imageUri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }
    private final ActivityResultLauncher <Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK){
                        Intent data = result.getData();
                        Log.d(TAG, "onActivityResult: imageUri: "+ imageUri);
                        imageIv.setImageURI(imageUri);
                    }else{
                        Toast.makeText(MediScanner.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );



    //OLDA Keine Ahnung muss schaun (brauche ich glaub ich nicht alles)
    private void extractBarCodeQRCodeInfo(List<Barcode> barcodes) {
        for(Barcode barcode: barcodes){
            Rect bounds= barcode.getBoundingBox();
            Point[] corners= barcode.getCornerPoints();

            String rawValue= barcode.getRawValue();
            Log.d(TAG, "extractBarCodeQRCodeInfos: rawValue: " + rawValue);

            int valueType= barcode.getValueType();

            switch (valueType){
                case Barcode.TYPE_WIFI:{
                    Barcode.WiFi typeWifi= barcode.getWifi();
                    String ssid=""+typeWifi.getSsid();
                    String password=""+typeWifi.getPassword();
                    String encryptionType=""+typeWifi.getEncryptionType();

                    Log.d(TAG, "extraBarCodeQRCodeInfo: ssid:  "+ ssid);
                    Log.d(TAG, "extraBarCodeQRCodeInfo: password:  "+ password);
                    Log.d(TAG, "extraBarCodeQRCodeInfo: encryptionType:  "+ encryptionType);

                    resultLabel.setText("TYPE: TYPE_WIFI \nssid: "+ssid+"\npassword: "+password+"\nencryptionType"+encryptionType+"\nraw value: "+ rawValue);
                }break;
                case Barcode.TYPE_URL:{
                    Barcode.UrlBookmark typeUrl= barcode.getUrl();

                    String title=""+typeUrl.getTitle();
                    String url=""+typeUrl.getUrl();

                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_URL");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: title: "+title);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: url: "+url);

                    resultLabel.setText("TYPE: TYPE_URL \ntitle: "+title+"\nurl: "+url+"\nraw value: "+ rawValue);
                }break;
                case Barcode.TYPE_EMAIL:{
                    Barcode.Email typeEmail= barcode.getEmail();

                    String address=""+typeEmail.getAddress();
                    String body=""+typeEmail.getBody();
                    String subject=""+typeEmail.getSubject();


                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_EMAIL");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: address: "+address);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: body: "+body);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: subject: "+subject);

                    resultLabel.setText("TYPE: TYPE_EMAIL \naddress: "+address+"\nbody: "+body+"\nsubject: "+subject+"\nraw value: "+ rawValue);
                }break;
                case Barcode.TYPE_CONTACT_INFO:{
                    Barcode.ContactInfo typeContact= barcode.getContactInfo();

                    String title=""+typeContact.getTitle();
                    String organizer=""+typeContact.getOrganization();
                    String name=""+typeContact.getName().getFirst()+" "+typeContact.getName().getLast();
                    String phones=""+typeContact.getPhones().get(0).getNumber();


                    Log.d(TAG, "extractBarCodeQRCodeInfo: TYPE_CONTACT_INFO");
                    Log.d(TAG, "extractBarCodeQRCodeInfo: title: "+title);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: organizer: "+organizer);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: name: "+name);
                    Log.d(TAG, "extractBarCodeQRCodeInfo: phones: "+phones);

                    resultLabel.setText("TYPE: TYPE_CONTACT_INFO \ntitle: "+title+"\norganizer: "+organizer+"\nname: "+name+"\nphones: "+phones+"\nraw value: "+ rawValue);
                }break;
                case Barcode.TYPE_PRODUCT: {
                    // Barcode an die Medipim-API senden und Informationen abrufen
                    String apiUrl = "https://api.medipim.be/v4/" + rawValue;

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(apiUrl)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    String jsonData = response.body().string();
                                    JSONObject jsonObject = new JSONObject(jsonData);

                                    // Hier die relevanten Informationen aus dem JSON extrahieren
                                    String productName = jsonObject.getString("name");
                                    String company = jsonObject.getJSONObject("manufacturer").getString("name");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            resultLabel.setText("Produkt: " + productName);
                                            resultTitel.setText("Firma: " + company);
                                        }
                                    });
                                } catch (JSONException e) {
                                    // Fehlerbehandlung für JSON-Analysefehler
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                            Log.e(TAG, "API request failed: " + e.getMessage());
                        }
                    });

                    break;
                }
                default: {
                            resultLabel.setText("raw value: " + rawValue);
                            //resultTitel.setText("rawValue: " + rawValue);
                }
            }
        }
    }

   /* private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
*/

    private boolean checkCameraPermission(){
        boolean resultCamera=ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean resultStorage=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        return resultCamera && resultStorage;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String [] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted= grantResults[0]==PackageManager.PERMISSION_GRANTED;
                   //boolean storageAccepted= grantResults[1]==PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted /*&& storageAccepted*/){
                        pickImageCamera();
                    }else{
                        Toast.makeText(this, "Camera permission is required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }break;
            /*case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted= grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickImageGallery();
                    }else{
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }break;*/
        }



    }



}