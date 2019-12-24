package com.kics.kstudio.kgallery.Asyn_Functions;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.CustomLoaderDialog;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by HP on 9/24/2018.
 */

public class RemoveMultiplePrivate {
    Context context;
    Activity activity;
    List<Photo_Item> data;
    GallerySqlite gallerySqlite;
    OnCompleteMultipleRemovePrivate listner;

    public interface OnCompleteMultipleRemovePrivate {
        public void onComplete();
    }

    public RemoveMultiplePrivate(Context context, List<Photo_Item> data, OnCompleteMultipleRemovePrivate listner) {
        this.context = context;
        this.activity= (Activity) context;
        this.data = data;
        this.listner = listner;
        gallerySqlite = new GallerySqlite(context);

    }

    public void executeTask() {
        RemoveMultiple removeMultiple = new RemoveMultiple();
        removeMultiple.execute();
    }

    public class RemoveMultiple extends AsyncTask<String, Void, String> {
        private CustomLoaderDialog customLoaderDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customLoaderDialog = new CustomLoaderDialog(context);
            customLoaderDialog.show();

            customLoaderDialog.setTitle("Removing Files");
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
                try {
                    gallerySqlite.RemovePrivatephoto(data.get(i), true);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final int finalI = i;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customLoaderDialog.setFiles(finalI + 1);
                    }
                });

            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            customLoaderDialog.dismiss();
            listner.onComplete();
            Constants.deletTempPrivates = false;
            Constants.back = true;
//            MainActivity.passwordNalagana=true;
        }
    }

}
