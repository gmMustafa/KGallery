package com.kics.kstudio.kgallery.Numpad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Manager.Data_Cache_Manager;
import com.kics.kstudio.kgallery.Manager.ManageActivity;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;
import com.kics.kstudio.phoneauth.PhoneNumberActivity;
import com.kics.kstudio.phoneauth.VerificationCodeActivity;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HP on 7/27/2018.
 */

public class GalleryNumPad extends FrameLayout implements View.OnClickListener {
    Integer counter = 0;
    Integer turnCount = 0;
    Integer timeCounter = 0;
    Boolean turn;
    String code = "";
    String code2 = "";
    Toast t;
    private ImageView num1;
    private ImageView num2;
    private ImageView num3;
    private ImageView num4;

    TextView b;

    Context context;

    public GalleryNumPad(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public GalleryNumPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public GalleryNumPad(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.gal_numpad, this);
        initViews();

    }

    void showNewPass() {
        TextView a = $(R.id.textView2);
        a.setText("Enter New Password");
        LovelyInfoDialog lovelyInfoDialog = new LovelyInfoDialog(context)
                .setTopColorRes(R.color.cardview_light_background)
                .setIcon(R.drawable.ic_info)
                //This will add Don't show again checkbox to the gal_dialog. You can pass any ID as argument
                 .setNotShowAgainOptionEnabled(0)
                 .setNotShowAgainOptionChecked(true)
                .setTitle("Set Up New Password")
                .setMessage("Set up a new password to get access.");
        lovelyInfoDialog.dismiss();
        lovelyInfoDialog.show();
    }

    private void initViews() {
        b = $(R.id.textView3);
        b.setVisibility(INVISIBLE);
        SharedPreferences prefs = getContext().getSharedPreferences("file", MODE_PRIVATE);
        turnCount = prefs.getInt("turn", 0);
        Integer verify = prefs.getInt("verify", 0);
        if (turnCount == 0) {
            turn = true;
            showNewPass();
        } else {
            if (verify == 0) {
                turn = true;
                showNewPass();
            } else {
                turn = false;
            }
        }

        num1 = $(R.id.imageview_block1);
        num2 = $(R.id.imageview_block2);
        num3 = $(R.id.imageview_block3);
        num4 = $(R.id.imageview_block4);

        num1.setImageResource(R.drawable.code_active);
        //mPasswordField = $(R.id.password_field);
        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        $(R.id.t9_key_clear).setOnClickListener(this);
        $(R.id.t9_key_backspace).setOnClickListener(this);
    }


    private void add(int id) {

        switch (id) {
            case R.id.t9_key_0:
                code += '0';
                break;
            case R.id.t9_key_1:
                code += '1';
                break;
            case R.id.t9_key_2:
                code += '2';
                break;
            case R.id.t9_key_3:
                code += '3';
                break;
            case R.id.t9_key_4:
                code += '4';
                break;
            case R.id.t9_key_5:
                code += '5';
                break;
            case R.id.t9_key_6:
                code += '6';
                break;
            case R.id.t9_key_7:
                code += '7';
                break;
            case R.id.t9_key_8:
                code += '8';
                break;
            case R.id.t9_key_9:
                code += '9';
                break;
        }

    }

    private void check() {
        if (code.length() == 4) {
            //check
            num1.setImageResource(R.drawable.code_not_enterd);
            num2.setImageResource(R.drawable.code_not_enterd);
            num3.setImageResource(R.drawable.code_not_enterd);
            num4.setImageResource(R.drawable.code_not_enterd);
            if (!turn) {
                code2 = code;
            }

            if (turn) {
                if (timeCounter == 0) {
                    code2 = code;
                    timeCounter++;
                }
                if (timeCounter == 2) {
                    //compare
                    if (code.equals(code2)) {
                        //save
                        SharedPreferences.Editor editor = getContext().getSharedPreferences("file", MODE_PRIVATE).edit();
                        editor.putInt("turn", 1);
                        editor.putString("pass", code);
                        editor.putString("MangeSpace", "1");
                        editor.apply();

                        SharedPreferences prefs = getContext().getSharedPreferences("file", MODE_PRIVATE);
                        if (prefs.getString("PhoneNumber", "0").equals("0")) {
                            editor.putInt("verify", 0);
                            editor.apply();
                           Intent intent = new Intent(context, PhoneNumberActivity.class);
                            context.startActivity(intent);
                            //here
                        } else {
                            editor.putInt("verify", 1);
                            editor.apply();
                        }

                        turnCount = prefs.getInt("turn", 0);
                        if (turnCount != 0) {
                            Constants.pass = true;
                            PrivateFragmentILock.ILock_unlock.Unlock();
                        }
                    } else {
                        code2 = "";
                        if (t != null) {
                            t.cancel();
                        }
                        t = Toast.makeText(getContext(), "Password not match", Toast.LENGTH_SHORT);
                        t.show();
                        timeCounter--;
                    }
                }
            }
            code = "";
            counter = 0;
        }
        if (turn && timeCounter == 1) {
            TextView a = $(R.id.textView2);
            a.setText("Confirm Password");
            timeCounter++;
            /*
             */
        }

        if (!turn) {
            SharedPreferences prefs = getContext().getSharedPreferences("file", MODE_PRIVATE);
            final String p = prefs.getString("pass", "0");
            if (p.equals(code2)) {
                if (getContext() instanceof ManageActivity) {
                    Intent intent = new Intent(getContext(), Data_Cache_Manager.class);
                    getContext().startActivity(intent);
                    ((ManageActivity) getContext()).finish();
                } else {

                    SharedPreferences.Editor editor = getContext().getSharedPreferences("passfile", MODE_PRIVATE).edit();
                    editor.putBoolean("LockStatus", true);
                    editor.apply();
                    Constants.pass = true;
                    PrivateFragmentILock.ILock_unlock.Unlock();
                }
            } else {
                if (t != null) {
                    t.cancel();
                }
                code2 = "";
                t = Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_SHORT);

                b = $(R.id.textView3);
                b.setVisibility(VISIBLE);
                b.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String code = "";
                        String num = "";
                        SharedPreferences prefs = getContext().getSharedPreferences("file", MODE_PRIVATE);
                        code = prefs.getString("PhoneCode", "0");
                        num = prefs.getString("PhoneNumber", "0");

                        Intent verificationIntent = new Intent(context, VerificationCodeActivity.class);
                        verificationIntent.putExtra("PhoneNumber", num.trim());
                        verificationIntent.putExtra("PhoneCode", code + "");
                        verificationIntent.putExtra("time", 1);
                        verificationIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        context.startActivity(verificationIntent);
                        //here2
                    }
                });

                t.show();
            }

        }


    }

    private void remove(int count) {
        if (counter == 0) {
            code = "";
        } else if (counter == 1) {
            code = code.substring(0, 0);
        } else if (counter == 2) {
            code = code.substring(0, 1);
        } else if (counter == 3) {
            code = code.substring(0, 2);
        }
    }

    @Override
    public void onClick(View v) {
        // handle number button click
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            // mPasswordField.append(((TextView) v).getText());
            if (counter == 0) {
                num1.setImageResource(R.drawable.code_enterd);
                num2.setImageResource(R.drawable.code_active);
                add(v.getId());
                counter++;
            } else if (counter == 1) {
                num2.setImageResource(R.drawable.code_enterd);
                num3.setImageResource(R.drawable.code_active);
                add(v.getId());
                counter++;
            } else if (counter == 2) {
                num3.setImageResource(R.drawable.code_enterd);
                num4.setImageResource(R.drawable.code_active);
                add(v.getId());
                counter++;
            } else if (counter == 3) {
                num4.setImageResource(R.drawable.code_enterd);
                add(v.getId());
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        check();
                    }
                }, 100);
            }

            return;
        }
        switch (v.getId()) {
            case R.id.t9_key_clear: { // handle clear button
                //    mPasswordField.setText(null);
            }
            break;
            case R.id.t9_key_backspace: { // handle backspace button
                remove(counter);
                if (counter == 1) {
                    num1.setImageResource(R.drawable.code_active);
                    num2.setImageResource(R.drawable.code_not_enterd);
                    counter--;
                } else if (counter == 2) {
                    num2.setImageResource(R.drawable.code_active);
                    num3.setImageResource(R.drawable.code_not_enterd);
                    counter--;
                } else if (counter == 3) {
                    num3.setImageResource(R.drawable.code_active);
                    num4.setImageResource(R.drawable.code_not_enterd);

                    counter--;
                }
            }
            break;
        }
    }

    public String getInputText() {
        return null;
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }
}