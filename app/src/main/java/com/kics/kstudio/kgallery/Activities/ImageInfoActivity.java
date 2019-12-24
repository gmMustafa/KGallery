package com.kics.kstudio.kgallery.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.kics.kstudio.kgallery.Adapters.PicInfoDataAdapter;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicDetailsInfo;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;


import java.util.ArrayList;
import java.util.List;

public class ImageInfoActivity extends AppCompatActivity {

    ListView listView;
    List<PicDetailsInfo> data;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gal_activity_image_info);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        Intent intent = getIntent();
        Photo_Item picData = (Photo_Item) intent.getSerializableExtra("Data");
        listView = (ListView) findViewById(R.id.list_info);


        data = new ArrayList<>();

        String title = picData.getKEY_TITLE();
        if (title.contains("/")) {
            title = title.substring(1);
        }

        data.add(new PicDetailsInfo("Title", title));
        data.add(new PicDetailsInfo("Type", picData.getKEY_TYPE()));
        data.add(new PicDetailsInfo("Album", picData.getKEY_ALBUM()));
        data.add(new PicDetailsInfo("Date", picData.getKEY_TIMESTAMP()));
        data.add(new PicDetailsInfo("Resolution", picData.getKEY_RESOLUTION()));
        data.add(new PicDetailsInfo("Size",
                String.format("%.2f",
                        Double.valueOf(picData.getKEY_SIZE().substring(0, picData.getKEY_SIZE().length() - 3)))
                        + picData.getKEY_SIZE().substring(picData.getKEY_SIZE().length() - 2))
        );
        if(!Constants.is_private)
        {
            data.add(new PicDetailsInfo("Path", picData.getKEY_OLD_PATH()));
        }

        PicInfoDataAdapter picInfoData = new PicInfoDataAdapter(this, data);
        listView.setAdapter(picInfoData);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finishactivity(null);
    }

    public void finishactivity(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishactivity(null);
    }
}
