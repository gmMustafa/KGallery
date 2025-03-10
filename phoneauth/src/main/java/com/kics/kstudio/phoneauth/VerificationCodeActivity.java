
package com.kics.kstudio.phoneauth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class VerificationCodeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Activity mActivity = VerificationCodeActivity.this;
    private AppCompatEditText etDigit1;
    private AppCompatEditText etDigit2;
    private AppCompatEditText etDigit3;
    private AppCompatEditText etDigit4;
    private AppCompatEditText etDigit5;
    private AppCompatEditText etDigit6;
    private Button btnContinue;
    private Button btnResendCode;
    private ImageView tvToolbarBack;
    private AppCompatTextView tvToolbarTitle;
    private AppCompatTextView tvCountDownTimer;
    private LinearLayout llContinue;
    private RelativeLayout rlResend;
    private ProgressBar pbVerify;
    private String strPhoneCode;
    private String strPhoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private CountDownTimer countDownTimer;
    Integer time = 0;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    Context context;


    void SiginFailed() {
        SharedPreferences.Editor editor = getSharedPreferences("file", MODE_PRIVATE).edit();
        if (time == 0) {
            editor.putInt("turn", 0);
        } else {
            editor.putInt("turn", 1);
        }
        editor.putInt("verify", 0);
        editor.putString("PhoneNumber", "0");
        editor.putString("PhoneCode", "0");
        editor.apply();
        checkWifi();
    }


    void SiginPass() {
        if (time != 0) {
            SharedPreferences.Editor editor = getSharedPreferences("file", MODE_PRIVATE).edit();
            editor.putInt("turn", 0);
            editor.putInt("verify", 0);
            editor.putString("PhoneNumber", strPhoneNumber);
            editor.putString("PhoneCode", strPhoneCode);
            editor.apply();
        } else {
            SharedPreferences.Editor editor = getSharedPreferences("file", MODE_PRIVATE).edit();
            editor.putInt("turn", 1);
            editor.putInt("verify", 1);
            editor.putString("PhoneNumber", strPhoneNumber);
            editor.putString("PhoneCode", strPhoneCode);
            editor.apply();

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ph_activity_verification_code);
        context = this;


        checkWifi();
        setUpUI();
        setUpToolBar();
        setUpToolBar();

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(AppConstant.PhoneCode)) {
                strPhoneCode = getIntent().getStringExtra(AppConstant.PhoneCode);
            }
            if (getIntent().hasExtra(AppConstant.PhoneNumber)) {
                strPhoneNumber = getIntent().getStringExtra(AppConstant.PhoneNumber);
            }

            if (getIntent().hasExtra("time")) {
                time = getIntent().getIntExtra("time", -1);
            }

            tvToolbarBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            tvToolbarTitle.setText(AppConstant.PLUS + strPhoneCode + " " + strPhoneNumber + "");
        }
        //FirebaseApp.initializeApp(VerificationCodeActivity.this);
        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Utility.log("onVerificationCompleted: " + credential);
                signInWithPhoneAuthCredential(credential);
                pbVerify.setVisibility(View.GONE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Utility.log("onVerificationFailed" + e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(context, "Limit Reached for Today.", Toast.LENGTH_SHORT).show();
                }
                SiginFailed();
                pbVerify.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Utility.log("onCodeSent: " + verificationId);
                Utility.log("token: " + token);
                pbVerify.setVisibility(View.GONE);
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };
        startPhoneNumberVerification(AppConstant.PLUS + strPhoneCode + strPhoneNumber + "");


    }

    private void checkWifi() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifi != null;
        wifi.setWifiEnabled(true);
        if (!wifi.isWifiEnabled()) {
            buildAlertMessageNoWIFI();
        }
    }

    private void buildAlertMessageNoWIFI() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Your WIFI seems to be disconnect, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        //      alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
        alert.show();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Utility.log("signInWithCredential:success");
                            pbVerify.setVisibility(View.GONE);
                            final FirebaseUser user = task.getResult().getUser();
                            Utility.showToast(VerificationCodeActivity.this, user.getPhoneNumber() + " verified successfully");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.putExtra("PHONE_NUMBER", user.getPhoneNumber());
                                    setResult(1080, intent);
                                    SiginPass();
                                    finish();
                                }
                            }, 500);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            pbVerify.setVisibility(View.GONE);
                            Utility.log("signInWithCredential:failure " + task.getException());
                            Utility.showToast(VerificationCodeActivity.this, " Verification failed");
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            SiginFailed();
                            Intent intent = new Intent();
                            intent.putExtra("PHONE_NUMBER", "");
                            setResult(1080, intent);
                            finish();
                        }
                    }
                });
    }

    private void setUpUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rlResend = findViewById(R.id.rlResend);
        llContinue = findViewById(R.id.llContinue);
        llContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnContinue.isClickable())
                    btnContinue.performClick();
            }
        });
        pbVerify = findViewById(R.id.pbVerify);

        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                if (validate()) {
                    if (!TextUtils.isEmpty(mVerificationId)) {
                        verifyPhoneNumberWithCode(mVerificationId,
                                etDigit1.getText().toString().trim() +
                                        etDigit2.getText().toString().trim() +
                                        etDigit3.getText().toString().trim() +
                                        etDigit4.getText().toString().trim() +
                                        etDigit5.getText().toString().trim() +
                                        etDigit6.getText().toString().trim());
                    } else {
                        Utility.showToast(VerificationCodeActivity.this, "Verification id not received");
                    }
                }
            }
        });

        btnResendCode = findViewById(R.id.btnResendCode);
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                if (mResendToken != null)
                    resendVerificationCode(AppConstant.PLUS + strPhoneCode + strPhoneNumber, mResendToken);
                else {
                    Utility.showToast(VerificationCodeActivity.this, "Resend token null");
                    onBackPressed();
                }
            }
        });


        tvToolbarBack = findViewById(R.id.tvToolbarBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvCountDownTimer = findViewById(R.id.tvCountDownTimer);

        etDigit1 = findViewById(R.id.etDigit1);
        etDigit2 = findViewById(R.id.etDigit2);
        etDigit3 = findViewById(R.id.etDigit3);
        etDigit4 = findViewById(R.id.etDigit4);
        etDigit5 = findViewById(R.id.etDigit5);
        etDigit6 = findViewById(R.id.etDigit6);

        setButtonContinueClickbleOrNot();
        tvToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        etDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit2.requestFocus();
                }
            }
        });
        etDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit3.requestFocus();
                } else {
                    etDigit1.requestFocus();
                }
            }
        });
        etDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit4.requestFocus();
                } else {
                    etDigit2.requestFocus();
                }
            }
        });
        etDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit5.requestFocus();
                } else {
                    etDigit3.requestFocus();
                }
            }
        });
        etDigit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit6.requestFocus();
                } else {
                    etDigit4.requestFocus();
                }
            }
        });
        etDigit6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                } else {
                    etDigit5.requestFocus();
                }
            }
        });

        etDigit1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                } else {
                    if (etDigit1.getText().toString().trim().length() == 1) {
                        etDigit2.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit2.getText().toString().trim().length() == 0)
                        etDigit1.requestFocus();
                } else {
                    if (etDigit2.getText().toString().trim().length() == 1) {
                        etDigit3.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit3.getText().toString().trim().length() == 0)
                        etDigit2.requestFocus();
                } else {
                    if (etDigit3.getText().toString().trim().length() == 1) {
                        etDigit4.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit4.getText().toString().trim().length() == 0)
                        etDigit3.requestFocus();
                } else {
                    if (etDigit4.getText().toString().trim().length() == 1) {
                        etDigit5.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit5.getText().toString().trim().length() == 0)
                        etDigit4.requestFocus();
                } else {
                    if (etDigit5.getText().toString().trim().length() == 1) {
                        etDigit6.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit6.getText().toString().trim().length() == 0)
                        etDigit5.requestFocus();
                }
                return false;
            }
        });

    }

    private boolean validate() {
        if (TextUtils.isEmpty(etDigit1.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit2.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit3.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit4.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit5.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit6.getText().toString().trim())) {
            return false;
        }
        return true;
    }

    private void setButtonContinueClickbleOrNot() {
        if (!validate()) {
            llContinue.setAlpha(.5f);
            btnContinue.setClickable(false);
        } else {
            llContinue.setAlpha(1.0f);
            btnContinue.setClickable(true);
        }
    }

    private void setUpToolBar() {
        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
    }


    private void signOut() {
        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        signOut();
        Intent intent = new Intent(mActivity, PhoneNumberActivity.class);
        intent.putExtra("TITLE", getResources().getString(R.string.app_name));
        intent.putExtra("PHONE_NUMBER", "");
        startActivity(intent);
        finish();
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
        startCounter();
    }


    private void startCounter() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDownTimer.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvCountDownTimer.setText("");
                btnResendCode.setEnabled(true);
                setResendButtonEnableDisable();
            }

        };
        countDownTimer.start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        startCounter();
        btnResendCode.setEnabled(false);
        setResendButtonEnableDisable();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pbVerify.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void setResendButtonEnableDisable() {
        if (btnResendCode.isEnabled()) {
            rlResend.setBackgroundResource(R.drawable.border_red_dark);
            btnResendCode.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else {
            rlResend.setBackgroundResource(R.drawable.border_red_light);
            btnResendCode.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        }
    }

}
