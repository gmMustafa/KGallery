package com.kics.kstudio.kgallery.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.kics.kstudio.kgallery.Activities.ListMarkers;
import com.kics.kstudio.kgallery.DataBase.LocationSqlite;
import com.kics.kstudio.kgallery.DataModels.MapMarkers;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;

/**
 * Created by HP on 7/31/2018.
 */

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MyViewHolder> {


    Context context;

    public interface helper {
        void setData(ArrayList<MapMarkers> data);
    }

    public static ArrayList<MapMarkers> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewLat;
        TextView textViewlng;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewTitle = (TextView) itemView.findViewById(R.id.title_info);
            this.textViewLat = (TextView) itemView.findViewById(R.id.lat_tv);
            this.textViewlng = (TextView) itemView.findViewById(R.id.lan_tv);

        }
    }

    public MarkerAdapter(ArrayList<MapMarkers> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gal_list_marker_view, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        TextView textViewTitle = holder.textViewTitle;
        TextView textViewLat = holder.textViewLat;
        TextView textViewlng = holder.textViewlng;

        textViewTitle.setText(dataSet.get(listPosition).getFIELD_TITLE());
        textViewLat.setText(dataSet.get(listPosition).getFIELD_LAT().toString().substring(0, 8));
        textViewlng.setText(dataSet.get(listPosition).getFIELD_LNG().toString().substring(0, 8));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Set the gal_dialog title

                builder.setTitle("DELETE")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        // Set the action buttons
                        .setPositiveButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(context, dataSet.get(listPosition).getFIELD_TITLE() + " Deleted", Toast.LENGTH_SHORT).show();
                                LocationSqlite locationSqlite = new LocationSqlite(context);
                                locationSqlite.deleteRecord(dataSet.get(listPosition).getFIELD_ROW_ID());
                                dataSet = (ArrayList<MapMarkers>) locationSqlite.getAllRecord();
                                if (dataSet.size() > 0) {
                                    ListMarkers.recyclerView.setAdapter(new MarkerAdapter(dataSet, context));
                                    ListMarkers.recyclerView.getAdapter().notifyDataSetChanged();
                                } else {
                                    ListMarkers.setnull();
                                }

                            }
                        });
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setDataSet(ArrayList<MapMarkers> data) {
        dataSet = data;
    }


    void setData(ArrayList<MapMarkers> data) {
        setDataSet(data);
    }
}
