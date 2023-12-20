package com.example.mediscanner_firebase.Christiane;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediscanner_firebase.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CalAdapter extends RecyclerView.Adapter<CalAdapter.ViewHolder> {

    private final ArrayList<DataClass> dataList;

    public CalAdapter(ArrayList<DataClass> dataList) {
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEvent, textViewZeit;
        public ImageButton deleteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewZeit = itemView.findViewById(R.id.anzeigeZeit);
            textViewEvent = itemView.findViewById(R.id.anzeigeEvent);
            deleteButton= itemView.findViewById(R.id.button);


        }


    }

    public interface OnDeleteItemClickListener {
        void onDeleteItemClick(DataClass dataItem);
    }

    private OnDeleteItemClickListener deleteItemClickListener;

    public void setOnDeleteItemClickListener(OnDeleteItemClickListener listener) {
        deleteItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataClass currentItem = dataList.get(position);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteItemClickListener != null) {
                    deleteItemClickListener.onDeleteItemClick(currentItem);
                }
            }
        });

        if (currentItem.getSelectedTimeMillis() == 0) {
            // Wenn selectedTimeMillis 0 ist, zeige "Ganztägig" an
            holder.textViewZeit.setText("Ganztägig");
        } else {
            // Ansonsten zeige die formatierte Zeit an
            String formattedTime = getFormattedTime(currentItem.getSelectedTimeMillis());
            holder.textViewZeit.setText(formattedTime);
        }


        holder.textViewEvent.setText(currentItem.getUploadEvent());

    }
    // Methode zur Umwandlung des long-Werts in ein lesbares Datum/Zeit-Format
    private String getFormattedTime(long timestampMillis) {
        // Erstellen eines Date-Objekts aus dem Timestamp in Millisekunden
        Date date = new Date(timestampMillis);

        // Formatieren des Datums/der Zeit
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
        return dateFormat.format(date); // Gibt das Datum/Zeit im angegebenen Format zurück
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}