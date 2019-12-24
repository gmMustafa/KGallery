package com.kics.kstudio.kgallery.Asyn_Functions;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.CustomLoaderDialog;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * Created by HP on 9/24/2018.
 */

public class AddMultiplePrivate {
    Context context;
    List<Photo_Item> data;
    GallerySqlite gallerySqlite;
    OnCompleteMultiplePrivateAdd listner;
    Activity activity;

    public interface OnCompleteMultiplePrivateAdd {
        public void onComplete();
    }


    public AddMultiplePrivate(Context context, List<Photo_Item> data, OnCompleteMultiplePrivateAdd listner) {
        this.context = context;
        this.activity= (Activity) context;
        this.data = data;
        this.listner = listner;
        gallerySqlite = new GallerySqlite(context);

    }



    public void executeTask() {
        AddMultiple addMultiple = new AddMultiple();
        addMultiple.execute();
    }

    public class AddMultiple extends AsyncTask<String, Void, String> {
        private CustomLoaderDialog customLoaderDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            customLoaderDialog = new CustomLoaderDialog(context);
            customLoaderDialog.show();

            customLoaderDialog.setTitle("Adding Private Files");
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
                    //add to list
                    Constants.private_list.add(data.get(i));
                    Log.d("ffT", "doInBackground: o:" + Constants.favr8_list.size());
                    if (gallerySqlite.isFavr8(data.get(i).getKEY_OLD_PATH())) {
                        gallerySqlite.RemoveFvr8(data.get(i).getKEY_OLD_PATH());
                        Log.d("ffT", "doInBackground: b:" + Constants.favr8_list.size());
                        if (getIndex(data.get(i)) != -1) {
                            //check
                            Constants.favr8_list.remove(getIndex(data.get(i)));

                        }

                        Log.d("ffT", "doInBackground: a:" + Constants.favr8_list.size());
                        /*    Constants.RemovePositionsFromVP.add(data.get(i));
                        Constants.change_in_favr8_fragment = true;
                    */

                    }
                    gallerySqlite.addPrivatephoto(data.get(i));
                    final int finalI = i;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            customLoaderDialog.setFiles(finalI +1);
                        }
                    });

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            customLoaderDialog.dismiss();

            Constants.deletTempPrivates = false;
            Constants.back = true;
            listner.onComplete();
        }

    }

    private int getIndex(Photo_Item photo_item) {
        for (int i = 0; i < Constants.favr8_list.size(); i++)
            if (Constants.favr8_list.get(i).getKEY_OLD_PATH().equals(photo_item.getKEY_OLD_PATH())) {
                return i;
            }
        return -1;
    }

}
