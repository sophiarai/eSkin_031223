package com.example.mediscanner_firebase.Yara;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        // Add a click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a pop-up window with the clinic data
                showPopup(markerOptions);
            }
        });

        return view;
    }

    private void showPopup(MarkerOptions markerOptions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(markerOptions.getTitle());
        builder.setMessage("Address: " + markerOptions.getSnippet());

        // Add more information such as phone number, etc.
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Actions to be performed when OK is clicked
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private MarkerOptions findMarkerOptions(Marker marker) {
        for (MarkerOptions options : markerOptionsList) {
            if (options.getPosition().equals(marker.getPosition())) {
                return options;
            }
        }
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Return null here, so that getInfoContents is called
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoView = inflater.inflate(R.layout.marker_info_window, null);

        MarkerOptions markerOptions = findMarkerOptions(marker);
        if (markerOptions != null) {
            String title = markerOptions.getTitle();
            String snippet = markerOptions.getSnippet();

            // Set the data in the view
            TextView titleTextView = infoView.findViewById(R.id.infoTitle);
            TextView snippetTextView = infoView.findViewById(R.id.infoSnippet);
            TextView oeffnungszeitenTextView = infoView.findViewById(R.id.infoOeffnungszeiten);

            titleTextView.setText(title);
            snippetTextView.setText(snippet);
        }

        return infoView;
    }
}
