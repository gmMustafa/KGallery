package com.kics.kstudio.kgallery.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kics.kstudio.kgallery.Adapters.AlbumPicsDateAdapter;
import com.kics.kstudio.kgallery.Adapters.Favr8PicsAdapter;
import com.kics.kstudio.kgallery.Adapters.PagerAdapter;
import com.kics.kstudio.kgallery.Adapters.PicsDateAdapter;
import com.kics.kstudio.kgallery.Adapters.Priva8PicsAdapter;
import com.kics.kstudio.kgallery.AsynTask_Load.LoadAlbumsThread;
import com.kics.kstudio.kgallery.AsynTask_Load.LoadNewTakenPic;
import com.kics.kstudio.kgallery.DataBase.LocationSqlite;
import com.kics.kstudio.kgallery.DataModels.MapMarkers;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Fragments.AlbumsFragment;
import com.kics.kstudio.kgallery.Fragments.FavoritesFragment;
import com.kics.kstudio.kgallery.Fragments.PhotosFragment;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Interfaces.I_ToolBarChange;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.CustomLoaderDialog;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;
import com.kics.kstudio.kgallery.Service.LocationMonitoringService;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DCIM;
import static com.kics.kstudio.kgallery.Fragments.AlbumsFragment.IUiSingleToMainAlbum;
import static com.kics.kstudio.kgallery.Fragments.AlbumsFragment.i_updateAlbums;
import static com.kics.kstudio.kgallery.Misc.Constants.PERMISSIONS;
import static com.kics.kstudio.kgallery.Misc.Constants.REQUEST_PERMISSION_KEY;
import static com.kics.kstudio.kgallery.Misc.Constants.lovelyStandardDialog;
import static com.kics.kstudio.kgallery.Misc.Constants.pass;

public class MainActivity extends AppCompatActivity
        implements I_ToolBarChange,
        View.OnClickListener {


    TabLayout tabLayout;
    public static TextView textView;
    public static ViewPager viewPager;
    public static PagerAdapter adapter;
    public static I_ToolBarChange i_toolbarchange;
    Integer countRegister = 0;
    Handler passwordHandler;


    List<String> tabs = new ArrayList<>();
    String TempFilePath = "";
    Uri mImageUri;
    private boolean isChecked;
    Intent serviceintent;
    private boolean alreadyFalse;
    private boolean inpass;
    private boolean alreadyTrue;
    private int firstTIme;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstTIme = 1;
        Constants.main_context = this;
        i_toolbarchange = this;
        initViews();
        setUi();

    }


    public void hide_app(final Context context) {
        lovelyStandardDialog = new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.cardview_dark_background)
                .setButtonsColorRes(R.color.red)
                .setIcon(R.drawable.ic_info)
                .setTitle("Hide APP")
                .setMessage("Press yes to continue.On pressing OK your application will be hide.to re open application" +
                        " dial 3020 from Dial pad??")
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = context.getSharedPreferences("file", MODE_PRIVATE).edit();
                        editor.putInt("turn", 1);
                        editor.putString("icon", "yes");
                        editor.apply();
                        PackageManager p = context.getPackageManager();
                        ComponentName componentName = new ComponentName(context, MainActivity.class);
                        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                        Toast.makeText(context, "Setting will take effect after restart.", Toast.LENGTH_SHORT).show();
                        lovelyStandardDialog.dismiss();
                    }
                })
                .setNegativeButton("NO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lovelyStandardDialog.dismiss();
                    }
                });

        lovelyStandardDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!Constants.is_timetoChangeTb) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem checkable = menu.findItem(R.id.loc_lock);
            if (isChecked) {
                checkable.setChecked(true);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.hide_app) {
            hide_app(MainActivity.this);
        }


        if (id == R.id.add_loc) {
            Intent intent = new Intent(MainActivity.this, ListMarkers.class);
            startActivity(intent);
        }
        if (id == R.id.loc_lock) {
            isChecked = !item.isChecked();
            item.setChecked(isChecked);
            if (isChecked) {
                Toast.makeText(getApplicationContext(), "Location Lock Enable", Toast.LENGTH_SHORT).show();
                savetofile("1");
                serviceintent = new Intent(new Intent(this, LocationMonitoringService.class));
                getApplicationContext().startService(serviceintent);
                if (countRegister != 1) {
                    getLocations();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Location Lock Disable", Toast.LENGTH_SHORT).show();
                savetofile("0");
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void initViews() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = findViewById(R.id.title);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        Constants.bundle = new Bundle();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }


    @SuppressLint("HandlerLeak")
    private void setUi() {
        textView.setText(getResources().getString(R.string.tab_photo));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_photo)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_album)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_favorite)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_private)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        tabs.add(getResources().getString(R.string.tab_photo));
        tabs.add(getResources().getString(R.string.tab_album));
        tabs.add(getResources().getString(R.string.tab_favorite));
        tabs.add(getResources().getString(R.string.tab_private));


        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.indicator_color));
        tabLayout.setTabTextColors(getResources().getColor(R.color.normal_color),
                getResources().getColor(R.color.selected_color));


        SharedPreferences pref2s = getSharedPreferences("file", MODE_PRIVATE);
        Integer turnCount = pref2s.getInt("turn", 0);
        if (turnCount == 0) {
            viewPager.setCurrentItem(3);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                textView.setText(tabLayout.getTabAt(tab.getPosition()).getText());

                SharedPreferences pref2s = getSharedPreferences("file", MODE_PRIVATE);
                Integer turnCount = pref2s.getInt("turn", 0);
                Integer verify = pref2s.getInt("verify", 0);
                if (turnCount == 0 || verify == 0) {
                    viewPager.setCurrentItem(3);
                }
                assert tab.getText() != null;

                if (tabs.contains(tab.getText())) {
                    if (Constants.single_album_pass) {
                        IUiSingleToMainAlbum.changeBack();
                    }
                }


                if (tab.getText().equals(getResources().getString(R.string.tab_photo))) {
                }
                if (tab.getText().equals(getResources().getString(R.string.tab_album))) {
                }
                if (tab.getText().equals(getResources().getString(R.string.tab_favorite))) {
                }
                if (tab.getText().equals(getResources().getString(R.string.tab_private))) {
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                CheckBoxes_Check();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.setOffscreenPageLimit(3);

        SharedPreferences prefs = getSharedPreferences("ServiceCheck", MODE_PRIVATE);
        String restoredText = prefs.getString("check", null);
        if (restoredText != null) {
            String service = prefs.getString("checkService", "0");//"No name defined" is the default value.
            if (service.equals("1")) {
                isChecked = true;
                serviceintent = new Intent(new Intent(this, LocationMonitoringService.class));
                getApplicationContext().startService(serviceintent);
                getLocations();
                countRegister = 1;
            }
        } else {
            SharedPreferences.Editor editor = getSharedPreferences("ServiceCheck", MODE_PRIVATE).edit();
            editor.putString("check", "1");
            editor.putString("checkService", "0");
            editor.apply();
        }

        passwordHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!((Boolean) msg.obj)) {
                    if (!alreadyFalse) {
                        inpass = false;
                        Constants.pass = false;
                        PrivateFragmentILock.ILock_unlock.Lock();
                        alreadyFalse = true;
                        alreadyTrue = false;
                    }
                } else {
                    if (!alreadyTrue) {
                        inpass = true;
                        Constants.pass = true;
                        PrivateFragmentILock.ILock_unlock.Unlock();
                        alreadyTrue = true;
                        alreadyFalse = false;
                    }
                }
            }
        };

    }


    public void openCamera(View view) {
        String mImagePath;
        if (Build.VERSION.SDK_INT <= 23) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, Constants.REQUEST_PERMISSION_KEY);
        } else {
            Log.d("CameraPhoto", "openCamera: ");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //yyyy-MM-dd HH:mm:ss
            //yyyyMMdd_HHmmss
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());
            File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath()
                    + "/Camera/IMG_" + timeStamp + ".jpg");

            TempFilePath = mImagePath = file.getAbsolutePath();
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mImagePath);
            mImageUri = MainActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (Utils.detectIntent(MainActivity.this, intent)) {
                Log.d("CameraPhoto", "openCamera: staring for result" + mImageUri.toString());
                setResult(RESULT_OK, intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri));
                startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_IMAGE);
            } else {
                Toast.makeText(MainActivity.this, "No photo App in this phone!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        Log.d("CameraPhoto", "onActivityResult: " + requestCode + resultCode);
        if (requestCode == Constants.REQUEST_CODE_TAKE_IMAGE && resultCode == RESULT_OK) {
            Log.d("CameraPhoto", "Request accepted");
            if (data != null && data.getExtras() != null) {
                Log.d("CameraPhoto", "data here");

                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                File file = new File(TempFilePath);
                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                assert imageBitmap != null;
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                try {
                    assert os != null;
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Utils.refreshSystemMediaScanDataBase(getApplicationContext(), file.getPath());
                addNewFileToCameraViaLoading();
            }
        }

        if (requestCode == Constants.REQUEST_PERMISSION_KEY && resultCode == RESULT_OK) {
            Log.d("CameraPhoto", "Request Accepted: ");
            addNewFileToCameraViaLoading();
        }
    }


    private void addNewFileToCameraViaLoading() {
        Log.d("CameraPhoto", "IN Function ");
        LoadNewTakenPic loadNewTakenPic = new LoadNewTakenPic(MainActivity.this, new LoadNewTakenPic.OnPhotoLoadingCompleteListener() {
            @Override
            public void onComplete(Photo_Item photo_item) {
                final Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                final String date = df.format(c);
                Log.d("CameraPhoto", "addNewFileToCamera: " + date);
                Log.d("CameraPhoto", "onComplete: " + photo_item.getKEY_OLD_PATH());
                if (Constants.Camera_photos_list.size() == 0) {
                    List<Photo_Item> list = new ArrayList<>();
                    list.add(photo_item);
                    PicsWithDates picsWithDates = new PicsWithDates(date, list);
                    Constants.Camera_photos_list.add(picsWithDates);
                    PicsDateAdapter.i_cameraUpdate.UpdateAll();

                } else {
                    Log.d("CameraPhoto", "addNewFileToCamera: " + date + " ldate:" + Constants.Camera_photos_list.get(0).getDate());
                    if (Constants.Camera_photos_list.get(0).getDate().equals(date)) {
                        Constants.Camera_photos_list.get(0).getPics().add(0, photo_item);
                        PicsDateAdapter.i_cameraUpdate.singleUpdate(0, 0);
                    } else {
                        List<Photo_Item> list = new ArrayList<>();
                        list.add(photo_item);
                        PicsWithDates picsWithDates = new PicsWithDates(date, list);
                        Constants.Camera_photos_list.add(0, picsWithDates);
                        PicsDateAdapter.i_cameraUpdate.UpdateAll();
                    }
                }


                if (Constants.Camera_photos_list.size() > 0) {
                    PhotosFragment.scrollerToZero.scroll();
                }
                changeAlbumsViews();
            }
        });
    }


    private void changeAlbumsViews() {
        new LoadAlbumsThread(Constants.main_context
                , new LoadAlbumsThread.OnAyscronusCallCompleteListener() {
            @Override
            public void onCompleteAlbumList(ArrayList<HashMap<String, String>> albumList) {
                Constants.albumList = albumList;
                if (i_updateAlbums != null) {
                    i_updateAlbums.update();
                    i_updateAlbums.updateInnerAlbum();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishapp(null);
        super.onBackPressed();
    }

    public void finishapp(View view) {
        if (Constants.single_album_pass) {
            IUiSingleToMainAlbum.changeBack();
            textView.setText(getResources().getString(R.string.tab_album));
        } else {
            finish();
        }
    }


    @Override
    public void ChangeTB() {
        Constants.is_timetoChangeTb = true;
        Toolbar toolbar2 = findViewById(R.id.toolbar2);
        toolbar2.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setTitle("");

        if (Constants.is_private) {
            ImageView imageView = toolbar2.findViewById(R.id.lock);
            imageView.setImageResource(R.drawable.ic_action_unlock);
        } else {
            ImageView imageView = toolbar2.findViewById(R.id.lock);
            imageView.setImageResource(R.drawable.ic_action_lock);
        }

    }

    @Override
    public void ChangeBTB() {
        Toolbar toolbar2 = findViewById(R.id.toolbar2);
        toolbar2.setVisibility(View.INVISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Constants.is_timetoChangeTb = false;
    }


    void CheckBoxes_Check() {
        Log.d("Box", "CheckBoxes_Check: " + Constants.Fragment_name);
        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_private))) {
            if (Constants.is_timetoChangeTb) {
                if (Constants.selection) {
                    Priva8PicsAdapter.private_notify.on_bx_Change();
                }
                Constants.selection = false;
                Constants.Selected_list.clear();
                Constants.positions.clear();
                i_toolbarchange.ChangeBTB();
            }
        }

        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_photo))) {
            Log.d("Box", "CheckBoxes_Check: " + Constants.is_timetoChangeTb);
            if (Constants.is_timetoChangeTb) {
                Constants.selection = false;
                Constants.Selected_list.clear();
                PicsDateAdapter.pa_ICheckBoxChange.on_bx_Change();
                Constants.positions.clear();
                i_toolbarchange.ChangeBTB();
            }

        }


        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_favorite))) {
            if (Constants.is_timetoChangeTb) {
                if (Constants.selection) {
                    if (Favr8PicsAdapter.favr8_cbxs != null)
                        Favr8PicsAdapter.favr8_cbxs.on_bx_Change();
                }
                Constants.selection = false;
                Constants.Selected_list.clear();
                Constants.positions.clear();
                i_toolbarchange.ChangeBTB();
            }

        }
        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_album))) {
            if (Constants.single_album_pass) {
                if (Constants.is_timetoChangeTb) {
                    Constants.selection = false;
                    Constants.Selected_list.clear();
                    AlbumPicsDateAdapter.paAlbum_ICheckBoxChange.on_bx_Change();
                    Constants.positions.clear();
                    i_toolbarchange.ChangeBTB();
                }

            }
        }
    }


    void CheckDeletionMultiple() {
        Log.d("hhT", "CheckDeletion: " + Constants.Fragment_name);
        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_private))) {
            Constants.selection = false;
            if (Constants.removeOnly_removePrivae) {
                Priva8PicsAdapter.IMultiple.RemovePrivate();
            } else {
                Priva8PicsAdapter.IMultiple.RemoveData();
            }
            i_toolbarchange.ChangeBTB();
        }

        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_photo))) {
            Constants.selection = false;
            if (Constants.removeOnly_removePrivae) {
                PicsDateAdapter.IMultiple.RemoveDataAndPrivateize();
            } else {
                PicsDateAdapter.IMultiple.RemoveData();
            }
            i_toolbarchange.ChangeBTB();
        }

        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_album))) {
            if (Constants.single_album_pass) {
                Constants.selection = false;
                if (Constants.removeOnly_removePrivae) {
                    AlbumPicsDateAdapter.album_Multiple.RemoveDataAndPrivateize();
                } else {
                    AlbumPicsDateAdapter.album_Multiple.RemoveData();
                }
                i_toolbarchange.ChangeBTB();
            }
        }

        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_favorite))) {
            Constants.selection = false;
            if (Constants.removeOnly_removePrivae) {
                Favr8PicsAdapter.i_multiple_fvr8.RemoveDataAndPrivateize();
                changeAlbumsViews();
            } else {
                Favr8PicsAdapter.i_multiple_fvr8.RemoveData();
                changeAlbumsViews();
            }
            i_toolbarchange.ChangeBTB();

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                CheckBoxes_Check();
                break;
            case R.id.share_btn:
                Utils.sendMultipleData(MainActivity.this, Constants.Selected_list);
                CheckBoxes_Check();
                break;
            case R.id.delete_btn:
                Constants.removeOnly_removePrivae = false;
                if (Constants.Selected_list.size() > 0) {
                    lovelyStandardDialog = new LovelyStandardDialog(MainActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setTopColorRes(R.color.white)
                            .setButtonsColorRes(R.color.red)
                            .setIcon(R.drawable.ic_info)
                            .setTitle("Action")
                            .setMessage("Are You Sure You Want to Delete this items?")
                            .setPositiveButton("YES", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lovelyStandardDialog.dismiss();
                                    CheckDeletionMultiple();
                                }
                            })
                            .setNegativeButton("NO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    lovelyStandardDialog.dismiss();
                                }
                            });
                    lovelyStandardDialog.show();

                } else {
                    Toast.makeText(MainActivity.this, "No Item Selected", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lock_btn:
                Constants.removeOnly_removePrivae = true;
                if (Constants.Selected_list.size() > 0) {
                    if (Constants.is_private) {
                        lovelyStandardDialog = new LovelyStandardDialog(MainActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.white)
                                .setButtonsColorRes(R.color.red)
                                .setIcon(R.drawable.ic_info)
                                .setTitle("Action")
                                .setMessage("Are You Sure You Want to Remove these items from Private?")
                                .setPositiveButton("YES", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lovelyStandardDialog.dismiss();
                                        CheckDeletionMultiple();
                                    }
                                })
                                .setNegativeButton("NO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lovelyStandardDialog.dismiss();
                                    }
                                });
                        lovelyStandardDialog.show();
                    } else {
                        lovelyStandardDialog = new LovelyStandardDialog(MainActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                                .setTopColorRes(R.color.white)
                                .setButtonsColorRes(R.color.red)
                                .setIcon(R.drawable.ic_info)
                                .setTitle("Action")
                                .setMessage("Are You Sure You Want to Add these items as Private?")
                                .setPositiveButton("YES", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lovelyStandardDialog.dismiss();
                                        CheckDeletionMultiple();
                                    }
                                })
                                .setNegativeButton("NO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        lovelyStandardDialog.dismiss();
                                    }
                                });
                        lovelyStandardDialog.show();

                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Item Selected", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!inpass) {
            Constants.pass = false;
        } else {
            Constants.pass = true;
        }

        if (Constants.passwordNalagana) {
            Constants.pass = true;
        }

        if (IUiSingleToMainAlbum != null) {
            IUiSingleToMainAlbum.changeBack();
        }
        i_toolbarchange.ChangeBTB();
        SingleChecks();

        if (!Constants.back) {
            pass = false;
            Constants.deletTempPrivates = false;
        }
        Constants.back = false;


        if (!Constants.pass) {
            if (PrivateFragmentILock.ILock_unlock != null) {
                PrivateFragmentILock.ILock_unlock.Lock();
            }
            savetoLockStatus();
        }
        Constants.pass = false;
        Constants.passwordNalagana = false;
        Constants.deletTempPrivates = false;
    }

    private void SingleChecks() {
        if (Constants.change_in_favr8_fragment) {
            removeFvr8();
            addFavr8();
            if (FavoritesFragment.i_updateUI_fvr8 != null) {
                FavoritesFragment.i_updateUI_fvr8.update();
            }
            Constants.change_in_favr8_fragment = false;
        }

        if (Constants.change_in_photo_fragment) {
            if (Constants.remove_orRemoveandPrivatize) {
                PicsDateAdapter.IMultiple.RemoveDataAndPrivateize();
            } else {
                PicsDateAdapter.IMultiple.RemoveData();
            }
            Constants.change_in_photo_fragment = false;
            Constants.remove_orRemoveandPrivatize = false;
        }

        if (Constants.change_in_albums) {
            Constants.positions.addAll(Constants.Tempositions);
            Constants.Selected_list.addAll(Constants.TempSelected_list);

            Constants.Tempositions.clear();
            Constants.TempSelected_list.clear();

            if (Constants.remove_orRemoveandPrivatize) {
                if (AlbumPicsDateAdapter.album_Multiple != null) {
                    AlbumPicsDateAdapter.album_Multiple.RemoveDataAndPrivateize();
                }
            }
        } else {
            if (AlbumPicsDateAdapter.album_Multiple != null) {
                AlbumPicsDateAdapter.album_Multiple.RemoveData();
            }
            Constants.remove_orRemoveandPrivatize = false;
            Constants.change_in_albums = false;
        }

        if (Constants.changeinPrivate) {
            Constants.changeinPrivate = false;
            Utils.Toast(MainActivity.this, "P:" + Constants.PrivatesToRemove.size());
            Log.d("", "SingleChecks: " + Constants.PrivatesToRemove.size());
            for (int i = 0; i < Constants.PrivatesToRemove.size(); i++) {
                Constants.private_list.remove(Constants.PrivatesToRemove.get(i));
            }

            if (PrivateFragmentILock.i_updateUI != null) {
                PrivateFragmentILock.i_updateUI.update();
            }
        }


        //after Removing from Private adding photos back to thier Locations
        if (Constants.isChangeAlbum) {
            changeAlbumsViews();
            Constants.isChangeAlbum = false;
        }

        if (Constants.isChangeCamera) {
            if (PicsDateAdapter.i_cameraUpdate != null) {
                PicsDateAdapter.i_cameraUpdate.NotifyAllChanges();
            }
            changeAlbumsViews();
            Constants.isChangeCamera = false;
        }

    }


    private void addFavr8() {
        Log.d("ffT", "addFavr8: " + Constants.AddFavr8.size());
        Constants.favr8_list.addAll(Constants.AddFavr8);
        Constants.AddFavr8.clear();
        Log.d("ffT", "addFavr8:After " + Constants.AddFavr8.size());
    }

    private void removeFvr8() {
        Log.d("ffT", "Before removeFvr8: " + Constants.favr8_list.size()
                +"td:"+Constants.RemovePositionsFromVP.size()
        );
        if (Constants.DeleteAlso) {
            for (int i = 0; i < Constants.RemovePositionsFromVP.size(); i++) {
                Log.d("ffT", "Before removeFvr8: " + Constants.RemovePositionsFromVP.get(i).getKEY_TIMESTAMP()+" " +
                        " "+Constants.RemovePositionsFromVP.get(i).getKEY_OLD_PATH());
                DeleteFromFavroiteFragment(Constants.RemovePositionsFromVP.get(i));
            }
            Constants.DeleteAlso = false;
        } else {
            Constants.favr8_list.removeAll(Constants.RemovePositionsFromVP);
        }

        if (Constants.isChangeAlbum) {
            changeAlbumsViews();
            Constants.isChangeAlbum = false;
        }

        if (Constants.isChangeCamera) {
            if (PicsDateAdapter.i_cameraUpdate != null) {
                PicsDateAdapter.i_cameraUpdate.NotifyAllChanges();
            }
            Constants.isChangeCamera = false;
        }


        Log.d("ffT", "after removeFvr8: " + Constants.favr8_list.size());
        Constants.RemovePositionsFromVP.clear();
    }


    public void savetofile(String val) {
        SharedPreferences.Editor editor = getSharedPreferences("ServiceCheck", MODE_PRIVATE).edit();
        editor.putString("checkService", val);
        editor.apply();
    }

    void getLocations() {

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                        if (!isChecked) {
                            Message message = new Message();
                            message.obj = false;
                            passwordHandler.sendMessage(message);

                            if (serviceintent != null) {
                                getApplicationContext().stopService(serviceintent);
                            }
                            serviceintent = null;

                        } else {

                            Boolean wifi, gps;
                            wifi = gps = true;

                            LocationManager locationManager;
                            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            assert connManager != null;
                            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                            if (!mWifi.isConnected()) {
                                wifi = false;
                            } else {
                                assert locationManager != null;
                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    gps = false;

                                }
                            }

                            if (wifi && gps) {
                                if (latitude != null && longitude != null) {
                                    Double lng = Double.parseDouble(longitude);
                                    Double lat = Double.parseDouble(latitude);

                                    if (lockCheck(lng, lat)) {

                                        Message message = new Message();
                                        message.obj = true;
                                        passwordHandler.sendMessage(message);

                                    } else {
                                        Message message = new Message();
                                        message.obj = false;
                                        passwordHandler.sendMessage(message);
                                    }
                                }
                            } else {
                                Message message = new Message();
                                message.obj = false;
                                passwordHandler.sendMessage(message);
                                if (serviceintent != null) {
                                    getApplicationContext().stopService(serviceintent);
                                }
                                serviceintent = null;
                            }
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );
    }


    public Boolean lockCheck(Double lng, Double lat) {
        LocationSqlite locationSqlite = new LocationSqlite(this);
        ArrayList<MapMarkers> mapList;
        mapList = (ArrayList<MapMarkers>) locationSqlite.getAllRecord();

        for (int i = 0; i < mapList.size(); i++) {
            Location center = new Location("fused");
            center.setLongitude(mapList.get(i).getFIELD_LNG());
            center.setLatitude(mapList.get(i).getFIELD_LAT());

            Location test = new Location("fused");
            test.setLatitude(lat);
            test.setLongitude(lng);
            Float distanceInMeters = center.distanceTo(test);
            boolean isWithin1km = distanceInMeters < 100;
            if (isWithin1km) {
                return true;
            }
        }
        return false;
    }
                /*
                change.ChangeBTB();
                Constants.Selected_list.clear();
                Constants.positions.clear();
                Constants.selection = false;
                */


    private void savetoLockStatus() {
        SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("passfile", MODE_PRIVATE).edit();
        editor.putBoolean("LockStatus", false);
        editor.apply();
    }


    void deleteTempFiles() {
        List<File> tempFileList = new ArrayList<>();
        if (Constants.private_list != null) {
            for (int i = 0; i < Constants.private_list.size(); i++) {
                File f = new File(Constants.private_list.get(i).getKEY_OLD_PATH());
                tempFileList.add(f);
            }
            for (File file : tempFileList) {
                file.delete();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (!Constants.deletTempPrivates && firstTIme != 1) {
            deleteTempFiles();
        }
        firstTIme = 0;
        super.onSaveInstanceState(savedInstanceState);

    }


    private void DeleteFromFavroiteFragment(Photo_Item photo_item) {
        String album = getALbumfromPath(photo_item.getKEY_OLD_PATH());
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + album);
        if (album.equals("Camera")) {
            DeleteFvr8FromCameraFragment(photo_item);
        } else {
            DeleteFvr8FromOtherFragment(album, photo_item);
        }
    }


    private void DeleteFvr8FromOtherFragment(String album, Photo_Item photo_item) {
        List<PicsWithDates> list = Utils.getAlbumSearch(album);
        assert list != null;
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + photo_item.getKEY_TIMESTAMP().substring(0, 10));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDate().equals(photo_item.getKEY_TIMESTAMP().substring(0, 10))) {
                for (int k = 0; i < list.get(i).getPics().size(); k++) {
                    if (list.get(i).getPics().get(k).getKEY_OLD_PATH().equals(photo_item.getKEY_OLD_PATH())) {
                        list.get(i).getPics().remove(k);
                        Constants.isChangeAlbum = true;
                        break;
                    }
                }
                break;
            }
        }
    }

    private void DeleteFvr8FromCameraFragment(Photo_Item photo_item) {
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + photo_item.getKEY_TIMESTAMP().substring(0, 10));

        for (int i = 0; i < Constants.Camera_photos_list.size(); i++) {
            if (Constants.Camera_photos_list.get(i).getDate().equals(photo_item.getKEY_TIMESTAMP().substring(0, 10))) {
                for (int k = 0; i < Constants.Camera_photos_list.get(i).getPics().size(); k++) {
                    if (Constants.Camera_photos_list.get(i).getPics().get(k).getKEY_OLD_PATH().equals(photo_item.getKEY_OLD_PATH())) {
                        Constants.Camera_photos_list.get(i).getPics().remove(k);
                        Constants.isChangeCamera=true;
                        Constants.isChangeAlbum = true;
                        Log.d("CSFEWA", "SearchForMainPositionInCamera: Removed" + photo_item.getKEY_OLD_PATH());
                        break;
                    }
                }
                break;
            }
        }
    }


    private String getALbumfromPath(String key_sec_path) {
        String reverse = key_sec_path;
        String album = "";
        for (int i = reverse.lastIndexOf('/'); i > 0; i--) {
            if (reverse.charAt(i) == '/') {
                i--;
                while (reverse.charAt(i) != '/') {
                    album += reverse.charAt(i);
                    i--;
                }
                break;
            }
        }
        String final_album = "";
        for (int i = album.length() - 1; i >= 0; i--) {
            final_album += album.charAt(i);
        }
        return final_album;
    }
}

