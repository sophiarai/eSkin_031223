package com.example.mediscanner_firebase.Yara;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.mediscanner_firebase.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MarkerAdapter extends BaseAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private List<MarkerOptions> markerOptionsList;
    private LayoutInflater inflater;

    private TextView snippetTextView;
    public MarkerAdapter(Context context, List<MarkerOptions> markerOptionsList) {
        this.context = context;
        this.markerOptionsList = markerOptionsList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return markerOptionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return markerOptionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_marker, null);
        }

        final MarkerOptions markerOptions = markerOptionsList.get(position);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        titleTextView.setText(markerOptions.getTitle());

        // Füge einen Klick-Listener hinzu
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Öffne ein Pop-up-Fenster mit den Daten der Ordination
                showPopup(markerOptions);
            }
        });

        return view;
    }

    private void showPopup(MarkerOptions markerOptions) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(markerOptions.getTitle());
        builder.setMessage("Adresse: " + markerOptions.getSnippet());

        // Füge weitere Informationen hinzu, wie Telefonnummer usw.

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Aktionen, die beim Klicken auf OK ausgeführt werden können
            }
        });

        // Erstelle und zeige den Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private MarkerOptions findMarkerOptions(Marker marker) {
        // Find the MarkerOptions that correspond to this marker
        for (MarkerOptions options : markerOptionsList) {
            if (options.getPosition().equals(marker.getPosition())) {
                return options;
            }
        }
        return null;
    }

    // Implement InfoWindowAdapter methods

    @Override
    public View getInfoWindow(Marker marker) {
        // Return null here, so that getInfoContents is called
        return null;
    }


    private String extractPhoneNumber(String snippet) {
        // Hier musst du die Telefonnummer aus dem Snippet extrahieren
        // Du kannst z.B. eine einfache Logik verwenden, um die Telefonnummer zu finden

        // Beispiel: Suche nach "Telefonnummer:" im Snippet und extrahiere den Rest als Telefonnummer
        if (snippet.contains("Telefonnummer:")) {
            int startIndex = snippet.indexOf("Telefonnummer:") + 14;
            int endIndex = snippet.indexOf("\n", startIndex);
            if (endIndex == -1) {
                endIndex = snippet.length();
            }
            return snippet.substring(startIndex, endIndex).trim();
        }

        return null; // Gib null zurück, wenn keine Telefonnummer gefunden wurde
    }


    @Override
    public View getInfoContents(Marker marker) {
        Log.d("InfoWindow", "getInfoContents called");

        View infoView = inflater.inflate(R.layout.marker_info_window, null);

        // Find the corresponding data for the marker in your list
        MarkerOptions markerOptions = findMarkerOptions(marker);
        String snippet = null;
        if (markerOptions != null) {
            String title = markerOptions.getTitle();
            snippet = markerOptions.getSnippet();

            // Set the data in the view
            TextView titleTextView = infoView.findViewById(R.id.infoTitle);
            TextView snippetTextView = infoView.findViewById(R.id.infoSnippet);
            TextView oeffnungszeitenTextView = infoView.findViewById(R.id.infoOeffnungszeiten);

            titleTextView.setText(title);
            snippetTextView.setText(snippet);

        }

        // Telefonnummer holen
        final String telefonnummer = extractPhoneNumber(snippet);

        // Wenn Telefonnummer vorhanden ist, füge einen Click Listener hinzu
        if (!TextUtils.isEmpty(telefonnummer)) {
            snippetTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Hier kannst du die gewünschte Aktion ausführen, z.B. Anruf oder SMS
                    // Hier ist ein Beispiel für einen Anruf:
                    // startPhoneCall(telefonnummer);

                    // Hier ist ein Beispiel für das Öffnen der SMS-Anwendung:
                    startSmsIntent(telefonnummer);
                }
            });
        }
        return infoView;
    }


    // Beispiel für das Öffnen der SMS-Anwendung
    private void startSmsIntent(String phoneNumber) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:" + phoneNumber));
        context.startActivity(smsIntent);
    }

}

