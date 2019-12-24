package com.kics.kstudio.kgallery.AsynTask_Load;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;


import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.Misc.ColumnIndexCache;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.MapComparator;
import com.kics.kstudio.kgallery.Misc.Utils;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LoadPhotosInAlbums {
    private String albumtofind;
    private Context context;
    private ArrayList<HashMap<String, String>> picList = new ArrayList<HashMap<String, String>>();
    private OnAyscronusCallCompleteListener listener;

    public LoadPhotosInAlbums(Context context, String Album, OnAyscronusCallCompleteListener listener) {
        this.context = context;
        this.listener = listener;
        albumtofind = Album;
        new LoadCameraPhotosAsyn().execute();
    }


    public interface OnAyscronusCallCompleteListener {
        public void onCompleteList(List<PicsWithDates> list, String album_name);
    }


    public class LoadCameraPhotosAsyn extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            picList.clear();
        }

        protected String doInBackground(String... args) {
            String albumFound = albumtofind;

            String xml = "";

            String path = null;
            String timestamp = null;
            String type;
            String title;
            String resolution;
            String size;
            String album;

            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            try {
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
                        + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{albumFound}, null);


                ColumnIndexCache cache = new ColumnIndexCache();

//                Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal});//, cursorInternal

                assert cursor != null;
                while (cursor.moveToNext()) {
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
                        Log.d("Type", type);
                        if (Utils.getSize(size).charAt(0) != '0') {
                            picList.add(Utils.photo_mappingInbox(album, path, timestamp, size, resolution, type, title));
                        }
                    }
                }
                cache.clear();
                cache = null;
                cursor.close();


                Uri VuriExternal = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                try {
                    cache = new ColumnIndexCache();
                    String[] Vprojection =
                            {
                                    MediaStore.Video.Media.DATA,
                                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                                    MediaStore.Video.Media.SIZE,
                                    MediaStore.Video.Media.DISPLAY_NAME,
                                    MediaStore.Video.VideoColumns.DATE_TAKEN,
                                    MediaStore.Video.VideoColumns.HEIGHT,
                                    MediaStore.Video.VideoColumns.WIDTH,
                                    MediaStore.MediaColumns.DATE_ADDED,
                                    MediaStore.MediaColumns.DATE_MODIFIED
                            };


                    Cursor Vcursor = context.getContentResolver().query(VuriExternal, Vprojection, "  "
                            + MediaStore.Video.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{albumFound}, null);

//                    Cursor Vcursor = new MergeCursor(new Cursor[]{VcursorExternal});//, VcursorInternal

                    assert Vcursor != null;
                    while (Vcursor.moveToNext()) {

                        path = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.Media.DATA));
                        if (Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.VideoColumns.DATE_TAKEN)) == null) {
                            timestamp = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.MediaColumns.DATE_ADDED));
                        } else {
                            timestamp = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.VideoColumns.DATE_TAKEN));
                        }
                        type = path.substring(path.lastIndexOf('.'));
                        title = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.Media.DISPLAY_NAME));
                        size = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.Media.SIZE));
                        if (size != null) {
                            if (Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.VideoColumns.HEIGHT)) == null) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(path, options);
                                int width = options.outWidth;
                                int height = options.outHeight;
                                resolution = String.valueOf(height) + '*' + String.valueOf(width);
                                File tempfile = new File(path);
                                size = (String.valueOf(tempfile.length()));
                            } else {
                                resolution = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.VideoColumns.HEIGHT))
                                        + " * " + Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.VideoColumns.WIDTH));
                            }
                            album = Vcursor.getString(cache.getColumnIndex(Vcursor, MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                            Log.d("Type", type);

                            if (Utils.getSize(size).charAt(0) != '0') {
                                picList.add(Utils.photo_mappingInbox(album, path, timestamp, size, resolution, type, title));
                            }

                        }
                    }
                    cache.clear();
                    cache = null;
                    Vcursor.close();
                } catch (Exception e) {
                    return "Cancel";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            List<PicsWithDates> picsWithDatesList;
            Collections.sort(picList, new MapComparator(Constants.KEY_TIMESTAMP, "desc"));
            picsWithDatesList = getpicWithdates(picList);
            getFvr8pics(picList);
            Log.d("Splash", "onPostExecute: Album:" + albumtofind + "   date:size:" + picsWithDatesList.size() + " p:size:" + picList.size());
            listener.onCompleteList(picsWithDatesList, albumtofind);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }


    private List<Photo_Item> getFvr8pics(ArrayList<HashMap<String, String>> picList) {

        if (Constants.paths != null) {
            for (int i = 0; i < Constants.paths.size(); i++) {
                for (int j = 0; j < picList.size(); j++) {
                    if (Constants.paths.get(i).equals(picList.get(j).get(Constants.KEY_PATH))) {
                        Photo_Item picData = new
                                Photo_Item(
                                picList.get(j).get(Constants.KEY_PATH),
                                picList.get(j).get(Constants.KEY_TIME)
                                , picList.get(j).get(Constants.KEY_TITLE)
                                , picList.get(j).get(Constants.KEY_ALBUM)
                                , picList.get(j).get(Constants.KEY_SIZE)
                                , picList.get(j).get(Constants.KEY_RESOLUTION)
                                , picList.get(j).get(Constants.KEY_TYPE), true);

                        if (picList.get(j).get(Constants.KEY_SIZE).charAt(0) != '0') {
                            Constants.favr8_list.add(picData);
                        }
                    }
                }
            }
        }
        return null;
    }

    private static List<PicsWithDates> getpicWithdates(ArrayList<HashMap<String, String>> picList) {
        List<PicsWithDates> picsWithDatesListInner = new ArrayList<>();
        List<Photo_Item> list = new ArrayList<>();


        if (picList.size() == 1) {
            Boolean fvr8 = false;
            /*if (gallerySqlite.isFavr8(picList.get(0).get(Constants.KEY_PATH))) {
                fvr8 = true;
            } else {
                fvr8 = false;
            }*/
            Photo_Item picData = new
                    Photo_Item(
                    picList.get(0).get(Constants.KEY_PATH),
                    picList.get(0).get(Constants.KEY_TIME)
                    , picList.get(0).get(Constants.KEY_TITLE)
                    , picList.get(0).get(Constants.KEY_ALBUM)
                    , picList.get(0).get(Constants.KEY_SIZE)
                    , picList.get(0).get(Constants.KEY_RESOLUTION)
                    , picList.get(0).get(Constants.KEY_TYPE), fvr8);


            list.add(picData);

            PicsWithDates picsWithDates = new PicsWithDates();
            picsWithDates.setDate(picList.get(0).get(Constants.KEY_TIME).substring(0, 10));
            picsWithDates.setPics(list);
            picsWithDatesListInner.add(picsWithDates);
        } else {

            Integer turn = 0;
            for (int i = 0; i < picList.size(); i++) {
                if (i != 0) {
                    String d1, d2;
                    d1 = picList.get(i).get(Constants.KEY_TIME).substring(0, 10);
                    d2 = picList.get(i - 1).get(Constants.KEY_TIME).substring(0, 10);
                    if (!d1.equals(d2)) {
                        turn++;
                        PicsWithDates picsWithDates = new PicsWithDates();
                        picsWithDates.setDate(d2);
                        picsWithDates.setPics(list);
                        picsWithDatesListInner.add(picsWithDates);
                        list = new ArrayList<>();
                    }

                    //last picture
                    if ((!d1.equals(d2)) && i == picList.size() - 1) {
                        turn++;
                        PicsWithDates picsWithDates = new PicsWithDates();
                        picsWithDates.setDate(d1);
                        picsWithDates.setPics(list);
                        picsWithDatesListInner.add(picsWithDates);
                    }
                    //same case till end
                    if ((d1.equals(d2)) && i == picList.size() - 1) {
                        turn++;
                        PicsWithDates picsWithDates = new PicsWithDates();
                        picsWithDates.setDate(d1);
                        picsWithDates.setPics(list);
                        picsWithDatesListInner.add(picsWithDates);
                    }
                }

                Boolean fvr8 = false;
               /* if (gallerySqlite.isFavr8(picList.get(i).get(Function.KEY_PATH))) {
                    fvr8 = true;
                } else {
                    fvr8 = false;
                }*/

                Photo_Item picData = new
                        Photo_Item(
                        picList.get(i).get(Constants.KEY_PATH),
                        picList.get(i).get(Constants.KEY_TIME)
                        , picList.get(i).get(Constants.KEY_TITLE)
                        , picList.get(i).get(Constants.KEY_ALBUM)
                        , picList.get(i).get(Constants.KEY_SIZE)
                        , picList.get(i).get(Constants.KEY_RESOLUTION)
                        , picList.get(i).get(Constants.KEY_TYPE), fvr8);
                list.add(picData);
            }

            if (turn == 0) {
                if (picList.size() > 0) {
                    PicsWithDates picsWithDates = new PicsWithDates();
                    picsWithDates.setDate(picList.get(0).get(Constants.KEY_TIME).substring(0, 10));
                    picsWithDates.setPics(list);
                    picsWithDatesListInner.add(picsWithDates);
                }
            }
        }
        return picsWithDatesListInner;
    }

}
