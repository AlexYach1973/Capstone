package org.coursera.sustainableapps.caostoneproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
    private final Boolean dataBase_Observe;

    // if bool = true - long text
    // if bool = false - short text
    private final Boolean long_short_text;

    public RecyclerViewAdapter(ArrayList<RecyclerObserveItem> arrayList, Context context,
                               Boolean dataBase_Observe, Boolean long_short_text) {
        this.arrayList = arrayList;
        this.context = context;
        this.dataBase_Observe = dataBase_Observe;
        this.long_short_text = long_short_text;

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public TextView textId;
        public TextView textLat;
        public TextView textLng;
        public TextView textDescription;
        public TextView textMeters;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_observe);
            textId = itemView.findViewById(R.id.text_id_observe);
            textLat = itemView.findViewById(R.id.text_lan_observe);
            textLng = itemView.findViewById(R.id.text_lng_observe);
            textDescription = itemView.findViewById(R.id.text_description_observe);
            textMeters = itemView.findViewById(R.id.text_meters_observe);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Call from Observe.class
            if (!dataBase_Observe) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        RecyclerObserveItem recyclerObserveItem = arrayList.get(position);

        //******************************* geters from RecyclerObserveItem

        // formation String int, Lat and Lng
        String strId = "id= " + recyclerObserveItem.getIdCurrent();

        String strLat = "lat= " +
                Math.round(recyclerObserveItem.getLat() * 100)/100.0;

        String strLng = "lon= " +
                Math.round(recyclerObserveItem.getLng() * 100)/100.0;

        holder.imageView.setImageResource(recyclerObserveItem.getImage());

        holder.textId.setText(strId);
        holder.textLat.setText(strLat);
        holder.textLng.setText(strLng);

        // if there is a short presentation of data, we make the id, Lat, long fields invisible
        if (!long_short_text) {
            holder.textId.setVisibility(View.GONE);
            holder.textLat.setVisibility(View.GONE);
            holder.textLng.setVisibility(View.GONE);
        }

        holder.textDescription.setText(recyclerObserveItem.getDescription());
        holder.textMeters.setText(recyclerObserveItem.getMeters());

        // call Observe.class
        if (!dataBase_Observe & !recyclerObserveItem.getMeters().equals("-- ")) {
            // vicinity check
            if (Integer.parseInt(recyclerObserveItem.getMeters()) < 100) {
                holder.textMeters.setTextColor(Color.parseColor("#FF3366"));

            } else {
                holder.textMeters.setTextColor(Color.parseColor("#BB86FC"));

            }
            holder.textMeters.setText(recyclerObserveItem.getMeters() + " m");
        }


        /**  ************* implementation context Menu ******************** */
        // Call from DataBase.class
        if (dataBase_Observe) {
            holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

                menu.setHeaderTitle(R.string.title_context_menu);

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

                            // delete element
                            holder.imageView.setImageResource(R.mipmap.crossbones);
//                            holder.textId.setVisibility(View.GONE);
                            holder.textId.setTextColor(Color.parseColor("#666666"));
//                            holder.textLat.setVisibility(View.GONE);
                            holder.textLat.setTextColor(Color.parseColor("#666666"));
//                            holder.textLng.setVisibility(View.GONE);
                            holder.textLng.setTextColor(Color.parseColor("#666666"));
                            holder.textMeters.setVisibility(View.GONE);
                            holder.textDescription.setText("  deleted item");
                            holder.textDescription.setTextColor(Color.RED);

                            // Logs
//                            Log.d("myLogs", "Description: "
//                                    + recyclerObserveItem.getDescription());

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
