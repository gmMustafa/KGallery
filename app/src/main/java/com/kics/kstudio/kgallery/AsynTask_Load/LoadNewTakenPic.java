package com.kics.kstudio.kgallery.AsynTask_Load;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.Misc.ColumnIndexCache;
import com.kics.kstudio.kgallery.Misc.Utils;

import java.io.File;


public class LoadNewTakenPic {
    private Context context;
    private OnPhotoLoadingCompleteListener listener;
    Photo_Item photo_item;

    public LoadNewTakenPic(Context context, OnPhotoLoadingCompleteListener listener) {
        this.context = context;
        this.listener = listener;

        new LoadSingleCameraTakenPic().execute();
    }


    public interface OnPhotoLoadingCompleteListener {
        public void onComplete(Photo_Item photo_item);
    }


    public class LoadSingleCameraTakenPic extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String albumFound = "Camera";

            String xml = "";

            String path = null;
            String timestamp = null;
            String type;
            String title = "";
            String resolution;
            String size;
            String album;

            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            try {
                Log.d("CameraPhoto", "doInBackground: ");
                String[] projection =
                        {
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                                MediaStore.Images.Media.SIZE,
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.ImageColumns.DATE_TAKEN,
                                MediaStore.Images.ImageColumns.HEIGHT,
                                MediaStore.Images.ImageColumns.WIDTH,
                                MediaStore.MediaColumns.DATE_ADDED,
                                MediaStore.MediaColumns.DATE_MODIFIED
                        };

                Cursor cursor = context.getContentResolver().query(uriExternal, projection, "  "
                                + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?"
                        , new String[]{albumFound}, MediaStore.Images.ImageColumns.DATE_TAKEN + " desc");
                ColumnIndexCache cache = new ColumnIndexCache();

                assert cursor != null;
                cursor.moveToFirst();
                Log.d("CameraPhoto", "doInBackground: " + cursor.getCount());
                while (true) {

                    Log.d("CameraPhoto", "doInBackground: in loop" );
                    path = cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.Media.DATA));
                    if (cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.ImageColumns.DATE_TAKEN)) == null) {
                        timestamp = cursor.getString(cache.getColumnIndex(cursor, MediaStore.MediaColumns.DATE_ADDED));
                    } else {
                        timestamp = cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.ImageColumns.DATE_TAKEN));
                    }
                    type = path.substring(path.lastIndexOf('.'));
                    title = cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.Media.DISPLAY_NAME));
                    size = cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.Media.SIZE));
                    if (size != null) {
                        if (cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.ImageColumns.HEIGHT)) == null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            BitmapFactory.decodeFile(path, options);
                            int width = options.outWidth;
                            int height = options.outHeight;
                            resolution = String.valueOf(height) + '*' + String.valueOf(width);
                            File tempfile = new File(path);
                            size = (String.valueOf(tempfile.length()));
                        } else {
                            resolution = cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.ImageColumns.HEIGHT))
                                    + " * " + cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.ImageColumns.WIDTH));
                        }
                        album = cursor.getString(cache.getColumnIndex(cursor, MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        Log.d("CameraPhoto", type);
                        if (Utils.getSize(size).charAt(0) != '0') {
                            photo_item = new Photo_Item(
                                    path,
                                    Utils.converToTime(timestamp),
                                    title,
                                    album,
                                    size,
                                    resolution,
                                    type,
                                    false);
                        }
                    }
                    cursor.moveToNext();
                    break;
                }
                cache.clear();
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            listener.onComplete(photo_item);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }

}
