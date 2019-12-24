package com.kics.kstudio.kgallery.Misc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Utils {
    private static Toast toast;

    public static boolean detectIntent(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static void refreshSystemMediaScanDataBase(Context context, String docPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(docPath));
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "hasPermissions: denied" + permission);
                    return false;
                }
            }
        }
        return true;
    }


    public static String getSize(String size) {
        String newSize = "";
        Double sizee = Double.valueOf(size);
        sizee = sizee / 1024;

        if (sizee > 1024) {
            sizee = sizee / 1024;
            newSize = sizee.toString() + " MB";
        } else {
            newSize = sizee.toString() + " KB";
        }
        return newSize;
    }


    public static String converToTime(String timestamp) throws ParseException {
        if (timestamp != null) {
            long datetime = Long.parseLong(timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date(datetime);
            Integer yearcheck = Integer.parseInt(dateFormat.format(date).substring(6, 10));
            if (yearcheck < 2000) {
                long dateMulti = Long.parseLong(timestamp);
                Date time = new java.util.Date((long) dateMulti * 1000);
                return String.valueOf(dateFormat.format(time));
            }
            return dateFormat.format(date);
        }
        return null;
    }


    public static HashMap<String, String> photo_mappingInbox(String album, String path, String timestamp,
                                                             String size, String res, String type, String title) throws ParseException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Constants.KEY_ALBUM, album);
        map.put(Constants.KEY_PATH, path);
        map.put(Constants.KEY_TIMESTAMP, timestamp);
        map.put(Constants.KEY_TIME, converToTime(timestamp));
        map.put(Constants.KEY_RESOLUTION, res);
        map.put(Constants.KEY_SIZE, getSize(size));
        map.put(Constants.KEY_TITLE, title);
        map.put(Constants.KEY_TYPE, type);
        return map;
    }

    public static HashMap<String, String> album_mappingInbox(String album, String path, String timestamp, String time, String count) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Constants.KEY_ALBUM, album);
        map.put(Constants.KEY_PATH, path);
        map.put(Constants.KEY_TIMESTAMP, timestamp);
        map.put(Constants.KEY_TIME, time);
        map.put(Constants.KEY_COUNT, count);
        return map;
    }


    public static HashMap<Integer, Integer> put_position(Integer parent, Integer child) {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(parent, child);
        return map;
    }

    public static Integer getCount(Context c, String album_name) {
        Integer counter = 0;
        Uri uriExternal = MediaStore.Files.getContentUri("external");
//        Uri uriInternal = MediaStore.Files.getContentUri("internal");

        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.SIZE
        };

        Cursor cursorExternal = c.getContentResolver().query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
        //Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal/*, cursorInternal*/});
        cursor.moveToFirst();

        Log.d("Albums", "count: " + cursor.getCount());

        int i = 0;
        while (!cursor.isAfterLast()) {

            Log.d("Albums", "itt: " + i);
            try {
                String type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
                String size = getSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));
                Log.d("Albums", "info: t:" + type + " s:" + size);
                if (type != null) {
                    if (type.substring(0, type.lastIndexOf('/')).equals("image")
                            || type.substring(0, type.lastIndexOf('/')).equals("video")) {
                        if (size.charAt(0) != '0') {
                            counter++;
                        }
                    }
                }
                Log.d("Albums", "Count: " + counter);
            } catch (Exception e) {
                Log.d("Albums", "excep: " + e.getMessage());
            } finally {
                cursor.moveToNext();
            }
            i++;
        }


        assert cursorExternal != null;
        cursorExternal.close();
    /*    assert cursorInternal != null;
        cursorInternal.close();
*/
        return counter;
    }

    public static List<PicsWithDates> getAlbumSearch(String album) {
        assert Constants.all_album_photos_list != null;

        for (int i = 0; i < Constants.all_album_photos_list.size(); i++) {
            if (Constants.all_album_photos_list.get(i).getAlbum().equals(album)) {
                return Constants.all_album_photos_list.get(i).getAlbum_photos_list();
            }
        }
        return null;
    }


    public static Integer getAlbumSearchIndex(String album) {
        assert Constants.all_album_photos_list != null;
        for (int i = 0; i < Constants.all_album_photos_list.size(); i++) {
            if (Constants.all_album_photos_list.get(i).getAlbum().equals(album)) {
                return i;
            }
        }
        return null;
    }


    public static void Toast(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void sendMultipleData(Context baseContext, List<Photo_Item> dataListtoProcess) {

        if (dataListtoProcess.size() > 0) {
            ArrayList<Uri> files = new ArrayList<Uri>();
            for (int i = 0; i < dataListtoProcess.size(); i++) {
                File file = new File(dataListtoProcess.get(i).getKEY_OLD_PATH());
                Uri uri = FileProvider.getUriForFile(baseContext, baseContext.getResources().getString(R.string.authority), file);
                files.add(uri);
            }

            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/jpeg");
            PackageManager pm = baseContext.getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, files);
                baseContext.startActivity(intent);
            }
            Constants.Selected_list.clear();
        } else {
            Toast.makeText(baseContext, "No Item Selected", Toast.LENGTH_SHORT).show();
        }
    }

}
