package com.kics.kstudio.kgallery.AsynTask_Load;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;


import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadAlbumsThread {
    static Context context;
    static ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    static LoadAlbumsThread.OnAyscronusCallCompleteListener album_listener;

    public LoadAlbumsThread(Context context, LoadAlbumsThread.OnAyscronusCallCompleteListener listener) {
        this.context = context;
        this.album_listener = listener;
        Constants.albumList.clear();
        new LoadAlbum().execute();
    }


    public interface OnAyscronusCallCompleteListener {
        public void onCompleteAlbumList(ArrayList<HashMap<String, String>> albumList);
    }

    class LoadAlbum extends AsyncTask<String, Void, String> {
        //        private ProgressDialog gal_dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;

            try {
                List<String> rep = new ArrayList<>();

                Uri uriExternalpics = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri uriExternalvideos = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                String[] projectionpics = {MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

                String[] projectionvideos = {MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

                Cursor cursorExternalpics = context.getContentResolver().query(uriExternalpics, projectionpics, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                        null, null);
                Cursor cursorpics = new MergeCursor(new Cursor[]{cursorExternalpics});//cursorInternalpics


                Cursor cursorExternalvideos = context.getContentResolver().query(uriExternalvideos, projectionvideos, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                        null, null);
                Cursor cursorvideos = new MergeCursor(new Cursor[]{cursorExternalvideos});//cursorInternalvideos


                Cursor MegaCursor = new MergeCursor(new Cursor[]{cursorpics, cursorvideos});

                while (MegaCursor.moveToNext()) {
                    path = MegaCursor.getString(cursorpics.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    album = MegaCursor.getString(cursorpics.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    Log.d("Albums", "doInBackground: "+path+"  "+album);
                    if (!rep.contains(album)) {
                        Integer c = Utils.getCount(context, album);
                        countPhoto = String.valueOf(c) + " items";
                        if (c > 0) {
                            albumList.add(Utils.album_mappingInbox(album, path, timestamp, Utils.converToTime(timestamp), countPhoto));
                            rep.add(album);
                        }
                    }
                }
                cursorpics.close();
                cursorExternalpics.close();

            } catch (Exception e) {
            }
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            album_listener.onCompleteAlbumList(albumList);

        }
    }

}
