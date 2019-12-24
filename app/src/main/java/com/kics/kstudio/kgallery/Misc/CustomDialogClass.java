package com.kics.kstudio.kgallery.Misc;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kics.kstudio.kgallery.R;


/**
 * Created by HP on 7/30/2018.
 */

public class CustomDialogClass extends Dialog implements
        View.OnClickListener {


    DialogListener listener;

    public interface DialogListener {
        void onCompleted();
        void onCanceled();
    }


    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    public Context c;
    public Dialog d;
    public Button yes, no;
    public TextView textView;
    public EditText editText;
    public String place;
    public Boolean ok = false;

    public CustomDialogClass(Context a) {
        super(a);
        // TODO Auto-generated constructor stubf
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gal_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        textView = findViewById(R.id.txt_dia);
        editText = (EditText) findViewById(R.id.editText2);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                place = editText.getText().toString();
                ok = true;
                Log.e("Check", place + "  " + ok);
                if (place.equals("") || place.isEmpty()) {
                    Toast.makeText(c, "INVALID", Toast.LENGTH_SHORT).show();
                } else {
                    if (listener != null) {
                        listener.onCompleted();
                        cancel();
                    }
                }
                break;
            case R.id.btn_no:
                dismiss();
                ok = false;
                if (listener != null)
                    listener.onCanceled();
                break;
            default:
                break;
        }
        dismiss();
    }
}
