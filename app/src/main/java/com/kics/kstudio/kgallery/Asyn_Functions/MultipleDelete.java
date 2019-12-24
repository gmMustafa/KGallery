package com.kics.kstudio.kgallery.Asyn_Functions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.kics.kstudio.kgallery.Activities.MainActivity;
import com.kics.kstudio.kgallery.AsynTask_Load.LoadAlbumsThread;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.Fragments.FavoritesFragment;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.CustomLoaderDialog;
import com.kics.kstudio.kgallery.Misc.Utils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kics.kstudio.kgallery.Fragments.AlbumsFragment.i_updateAlbums;


/**
 * Created by HP on 9/24/2018.
 */

public class MultipleDelete {
    Context context;
    Activity activity;
    private List<Photo_Item> data;
    private GallerySqlite gallerySqlite;
    OnCompleteMultipleDelete listner;

    public interface OnCompleteMultipleDelete {
        public void onComplete();
    }

    public MultipleDelete(Context context, List<Photo_Item> data, OnCompleteMultipleDelete listner) {
        this.context = context;
        this.activity= (Activity) context;
        this.data = data;
        this.listner = listner;
        gallerySqlite = new GallerySqlite(Constants.main_context);
    }

    public void executeTask() {
        DeleteMultiple deleteMultiple = new DeleteMultiple();
        deleteMultiple.execute();
    }

    public class DeleteMultiple extends AsyncTask<String, Void, String> {
        private CustomLoaderDialog customLoaderDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customLoaderDialog = new CustomLoaderDialog(context);
            customLoaderDialog.show();
            customLoaderDialog.setTitle("Deleting Files");
            customLoaderDialog.setTotalfiles(data.size());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    customLoaderDialog.setFiles(0);
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i < data.size(); i++) {
                if (gallerySqlite.isFavr8(data.get(i).getKEY_OLD_PATH())) {
                    gallerySqlite.RemoveFvr8(data.get(i).getKEY_OLD_PATH());
                    Constants.favr8_list.remove(data.get(i));
                }

                File file = new File(data.get(i).getKEY_OLD_PATH());
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                        file.delete();
                    } catch (IOException e) {
                        e.getMessage();
                    }
                    if (file.exists()) {
                        Constants.main_context.deleteFile(file.getName());
                    }
                }
                refreshSystemMediaScanDataBase(Constants.main_context, data.get(i).getKEY_OLD_PATH());
                final int finalI = i;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customLoaderDialog.setFiles(finalI +1);
                    }
                }); }
            return "ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            customLoaderDialog.dismiss();
            Constants.Selected_list.clear();
            Constants.positions.clear();
            Constants.removeOnly_removePrivae = false;
            changeAlbumsViews();


            Constants.back = true;
            Constants.deletTempPrivates = false;

            listner.onComplete();
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


        void refreshSystemMediaScanDataBase(Context context, String docPath) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(docPath));
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }
    }





}