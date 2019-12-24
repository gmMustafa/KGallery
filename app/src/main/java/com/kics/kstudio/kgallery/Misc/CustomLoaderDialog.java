package com.kics.kstudio.kgallery.Misc;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Asyn_Functions.AddMultiplePrivate;
import com.kics.kstudio.kgallery.R;


public class CustomLoaderDialog extends Dialog {

    private TextView title;
    private TextView percentage;
    public static TextView files;
    private ProgressBar progressBar;
    private int totalfiles;



    public CustomLoaderDialog(Context context) {
        super(context);
        Log.d("CCLSD", "Ctor: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_loading);
        title = findViewById(R.id.txt_title);
        files = findViewById(R.id.txt_items);
        percentage = findViewById(R.id.txt_percentage);
        progressBar = findViewById(R.id.progressBar);
    }


    public void setTitle(String txt_title) {
        this.title.setText(txt_title);
    }


    public void setFiles(final int complete) {
        files.setText(complete + "/" + totalfiles);
        if(totalfiles!=0)
        {
            int progress = (complete / totalfiles) * 100;
            setProgress(progress);
        }
        setProgress(100);
    }


    public void setTotalfiles(int totalfiles) {
        this.totalfiles = totalfiles;
    }

    private void setProgress(int progress) {
        this.progressBar.setProgress(progress);
        this.percentage.setText(progress + "%");
    }
}
