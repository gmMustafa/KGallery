package com.kics.kstudio.kgallery.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kics.kstudio.kgallery.AsynTask_Load.LoadAlbumsThread;
import com.kics.kstudio.kgallery.AsynTask_Load.LoadPhotosInAlbums;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.DataModels.Data;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kics.kstudio.kgallery.Misc.Constants.PERMISSIONS;
import static com.kics.kstudio.kgallery.Misc.Constants.REQUEST_PERMISSION_KEY;

public class Splash extends AppCompatActivity {

    //Handler msgHandler;
    GallerySqlite gallerySqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        gallerySqlite = new GallerySqlite(Splash.this);

        reset();
        Constants.paths = gallerySqlite.getFvr8();
        Constants.counter = 0;
        LoadSplashFinal();
       // getPermissions();
    }

    private void reset() {

        Constants.all_album_photos_list.clear();
        Constants.albumList.clear();
        Constants.Camera_photos_list.clear();
        Constants.private_list.clear();
        Constants.favr8_list.clear();
        Constants.Selected_list.clear();
        Constants.positions.clear();
        Constants.Current_album_photos_list.clear();

        //Temps
        Constants.RemovePositionsFromVP.clear();
        Constants.AddFavr8.clear();
        Constants.Tempositions.clear();
        Constants.TempSelected_list.clear();

    }

    void LoadSplashFinal() {
        new LoadAlbumsThread(Splash.this, new LoadAlbumsThread.OnAyscronusCallCompleteListener() {
            @Override
            public void onCompleteAlbumList(final ArrayList<HashMap<String, String>> albumList) {
                Constants.albumList = albumList;
                Log.d("SPLASH", "onCompleteList: " + "NOW 1  size:" + albumList.size());
                for (int i = 0; i < Constants.albumList.size(); i++) {
                    String albumtofind = albumList.get(i).get(Constants.KEY_ALBUM);
                    new LoadPhotosInAlbums(Splash.this, albumtofind, new LoadPhotosInAlbums.OnAyscronusCallCompleteListener() {
                        @Override
                        public void onCompleteList(List<PicsWithDates> list, String album_name) {
                            Constants.all_album_photos_list.add(new Data(album_name, list));
                            Constants.counter++;
                            if (album_name.equals("Camera")) {
                                Constants.Camera_photos_list = list;
                            }
                            if (Constants.counter == albumList.size()) {
                                Log.d("SPLASH", "onCompleteList: mine:");
                                startActivityFromMainThread();
                            }
                        }
                    });
                }
            }
        });
    }


    public void startActivityFromMainThread() {

        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    void getPermissions() {
        if (!Utils.hasPermissions(this, PERMISSIONS)) {
            Log.d("Permission", "FirstTime: ");
            ActivityCompat.requestPermissions(Splash.this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        } else {
            LoadSplashFinal();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                Log.d("Permission", "onRequestPermissionsResult: ");
                if (!Utils.hasPermissions(this, permissions)) {
                    ActivityCompat.requestPermissions(Splash.this, PERMISSIONS, REQUEST_PERMISSION_KEY);
                    Utils.Toast(Splash.this, "Permission Denied");
                } else {
                    LoadSplashFinal();
                }
            }
        }
    }


}
