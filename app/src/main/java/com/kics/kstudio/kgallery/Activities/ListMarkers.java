package com.kics.kstudio.kgallery.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Adapters.MarkerAdapter;
import com.kics.kstudio.kgallery.DataBase.LocationSqlite;
import com.kics.kstudio.kgallery.DataModels.MapMarkers;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;

public class ListMarkers extends AppCompatActivity implements View.OnClickListener {


    LocationSqlite locationSqlite;
    public static RecyclerView.Adapter adapter;

    private RecyclerView.LayoutManager layoutManager;
    public static RecyclerView recyclerView;
    private static ArrayList<MapMarkers> data;


    Button add;
    ImageView bck;
    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationSqlite = new LocationSqlite(this);
        setContentView(R.layout.gal_activity_list_markers);
        bck = (ImageView) findViewById(R.id.left_arrow);
        add = (Button) findViewById(R.id.add_btn);
        textView = (TextView) findViewById(R.id.n_found);
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        bck.setOnClickListener(this);
        add.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_arrow:
                onBackPressed();
                break;

            case R.id.add_btn:
                Intent intent = new Intent(ListMarkers.this, MapsActivity.class);
                startActivity(intent);
                break;
        }
    }

    public static void setnull() {
        recyclerView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        data = (ArrayList<MapMarkers>) locationSqlite.getAllRecord();
        if (data.size() > 0) {
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new MarkerAdapter(data, ListMarkers.this));

        } else {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean StatusCheck() {
        SharedPreferences prefs = ListMarkers.this.getSharedPreferences("passfile", MODE_PRIVATE);
        return prefs.getBoolean("LockStatus", false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishactivity(null);
    }


    public void finishactivity(View view) {
        if (StatusCheck()) {
            Constants.passwordNalagana = true;
        }
/*        MainActivity.deletenot = false;
        MainActivity.back = true;*/
        finish();
    }
}
