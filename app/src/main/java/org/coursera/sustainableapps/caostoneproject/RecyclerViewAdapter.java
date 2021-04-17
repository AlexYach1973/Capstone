package org.coursera.sustainableapps.caostoneproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private final ArrayList<RecyclerObserveItem> arrayList;
    private final Context context;

    // if bool = false - from Observe.class
    // if bool = true - from DataBase.class
    private final Boolean bool;

    public RecyclerViewAdapter(ArrayList<RecyclerObserveItem> arrayList, Context context,
                               Boolean bool) {
        this.arrayList = arrayList;
        this.context = context;
        this.bool = bool;

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//            , View.OnCreateContextMenuListener {

        public ImageView imageView;
        public TextView textId;
        public TextView textLan;
        public TextView textLng;
        public TextView textDescription;
        public TextView textMeters;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_observe);
            textId = itemView.findViewById(R.id.text_id_observe);
            textLan = itemView.findViewById(R.id.text_lan_observe);
            textLng = itemView.findViewById(R.id.text_lng_observe);
            textDescription = itemView.findViewById(R.id.text_description_observe);
            textMeters = itemView.findViewById(R.id.text_meters_observe);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Call from Observe.class
            if (!bool) {
                int position = getAdapterPosition();
                RecyclerObserveItem recyclerObserveItem = arrayList.get(position);

                // Строка для карты
                // String for the MapView
                String addressSelected = "http://www.google.com/maps/@" + recyclerObserveItem.getLat() +
                        "," + recyclerObserveItem.getLng() + "," + 15 + "z";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addressSelected));
                context.startActivity(intent);
            }
        }
    }

    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,
                parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        RecyclerObserveItem recyclerObserveItem = arrayList.get(position);

        // formation String int, Lat and Lng
        String strId = "id= " + recyclerObserveItem.getIdCurrent();

        String strLat = "lat= " +
                Math.round(recyclerObserveItem.getLat() * 100)/100.0;

        String strLng = "lon= " +
                Math.round(recyclerObserveItem.getLng() * 100)/100.0;

        //******************************* geters from RecyclerObserveItem
        holder.imageView.setImageResource(recyclerObserveItem.getImage());
        holder.textId.setText(strId);
        holder.textLan.setText(strLat);
        holder.textLng.setText(strLng);
        holder.textDescription.setText(recyclerObserveItem.getDescription());
        holder.textMeters.setText(recyclerObserveItem.getMeters());


        /**  ************* implementation context Menu ******************** */
        // Call from DataBase.class
        if (bool) {
            holder.textDescription.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

                menu.setHeaderTitle("Your choice");
                //**************************  CHANGE **************/
                menu.add(0, 1,0, "change")
                        .setOnMenuItemClickListener(item -> {

                            // new Intent
                           Intent intent = new Intent(context, Position.class);

                           // Fill intent
                            // id
                            intent.putExtra("id", recyclerObserveItem.getIdCurrent());
                           // Image Icon
                           intent.putExtra(DataBase.POSITION_DANGER,
                                   recyclerObserveItem.getImage());
                           // Latitude
                            intent.putExtra(DataBase.LATITUDE,
                                    recyclerObserveItem.getLat());
                            // Longitude
                            intent.putExtra(DataBase.LONGITUDE,
                                    recyclerObserveItem.getLng());
                            // Description
                            intent.putExtra(DataBase.DESCRIPTION,
                                    recyclerObserveItem.getDescription());

                            // Start Activity Position
                            context.startActivity(intent);

                            return true;
                        });

                //***************************  DELETE **************/
                menu.add(0, 2, 0, "delete")
                        .setOnMenuItemClickListener(item -> {

                            // Call method from DataBase
                            DataBase.deleteForId(recyclerObserveItem.getIdCurrent(), context);

                            Log.d("myLogs", "Description: "
                                    + recyclerObserveItem.getDescription());

                            return true;
                        });

            });
        }
        /**  ************* END implementation context Menu ******************** */
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
