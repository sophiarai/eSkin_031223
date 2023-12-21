package com.example.mediscanner_firebase.Verena;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mediscanner_firebase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Hinweis
 * Die Klasse "MyAdapter" fungiert als Adapter für die RecyclerView-Anzeige und erleichtert die Anzeige von Datenelementen in der App.
 * Sie ermöglicht die Anpassung und Darstellung von Daten in der RecyclerView unter Berücksichtigung der Nutzerinteraktion.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


    private Context context;
    private List<DataClass> dataList;

    /**
     * Initialisiert den Adapter mit dem Anwendungskontext und der Liste von Daten.
     * @param context:Der Kontext der Anwendung.
     * @param dataList: Eine Liste von Objekten des Typs DataClass, die die anzuzeigenden Daten repräsentieren.
     */

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    /**
     * Erstellt eine neue Instanz von MyViewHolder durch Inflation des Layouts
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }


    /**
     * Füllt die Ansichtselemente eines MyViewHolder mit den entsprechenden Daten aus der dataList. Nutzt die Glide-Bibliothek, um Bilder zu laden.
     * Definiert einen OnClickListener für die Kartenansichtselemente, um detaillierte Informationen der angeklickten Daten anzuzeigen.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDesc.setText(dataList.get(position).getDataDesc());
        holder.recLang.setText(dataList.get(position).getDataLang());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
                context.startActivity(intent);
            }
        });
    }

    /**
     * getItemCount() - Methode:
     * @return: Gibt die Anzahl der Elemente in der dataList zurück.
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * searchDataList-Methode
     * @param searchList:Aktualisiert die dataList mit einer neuen Liste von Daten und benachrichtigt den Adapter über Datenänderungen.
     */
    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}


/**
 *Innere Klasse MyViewHolder: Eine innere Klasse, die als benutzerdefinierter View Holder für den RecyclerView dient
 */
class MyViewHolder extends RecyclerView.ViewHolder{

    /**
     * Attribute:
     * @recImage, @recTitle, @recDesc, @recLang: Referenzen auf die Ansichtselemente für das Bild, den Titel, die Beschreibung und die Sprache der Daten.
     * @recCard: Eine Referenz auf das Kartenansichtselement.
     */
    ImageView recImage;
    TextView recTitle, recDesc, recLang;
    CardView recCard;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.recDesc);
        recLang = itemView.findViewById(R.id.recLang);
        recTitle = itemView.findViewById(R.id.recTitle);
    }
}