package com.kics.kstudio.kgallery.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


import com.kics.kstudio.kgallery.Adapters.PicsDateAdapter;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by HP on 8/20/2018.
 */

public class GallerySqlite {

    static String GlobalPath = "";
    private SQLiteDatabase database;
    private Helper dbHelper;
    private Context context;


    public GallerySqlite(Context context) {
        dbHelper = new Helper(context);
        this.context = context;
    }

    public List<String> getFvr8() {
        database = dbHelper.getWritableDatabase();
        List<String> paths = new ArrayList<>();
        Cursor cursor = database.query("Favorites", null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                paths.add(cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
            return paths;
        }
        return null;
    }

    public Boolean addFvr8(String Path) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String id = null;
        values.put("Pic_id", id);
        values.put("path", Path);

        if (isFavr8(Path)) {
            return false;
        }
        long check = database.insert("Favorites", null, values);
        if (check > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean RemoveFvr8(String Path) {
        database = dbHelper.getWritableDatabase();
        String[] whereArgs = {Path};
        long check = database.delete("Favorites", " path =?", whereArgs);
        if (check > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isFavr8(String Path) {
        database = dbHelper.getWritableDatabase();
        String[] whereArgs = {Path};
        Cursor cursor = database.query("Favorites", null, " path =?", whereArgs, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    public Boolean addPrivatephoto(Photo_Item data) throws NoSuchAlgorithmException, IOException {
        String path = data.getKEY_OLD_PATH();
        StrongAES app = new StrongAES();

        /*if(isFavr8(path))
        {
            if(RemoveFvr8(path))
         */

        byte[] enc;
        byte[] name;
        // save Encrpyted path
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull("Pic_id");
        //new name
        enc = app.encrypt(path.substring(path.lastIndexOf('/'), path.lastIndexOf('.')));
        values.put("new_name", enc);
        name = enc;

        //type
        values.put("type", path.substring(path.lastIndexOf('.')));
        //old enc path
        enc = app.encrypt(path);
        values.put("oldPath", enc);

        //new enc path
        //delete old picture
        CopyPicture(path, GlobalPath + "/" + path.substring(path.lastIndexOf('/')));

        //delete file
        File file = new File(path);
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
            }
            if (file.exists()) {
                context.deleteFile(file.getName());
            }
        }
        refreshSystemMediaScanDataBase(context, path);


        String newPath = GlobalPath + path.substring(path.lastIndexOf('/'));
        Random rand = new Random();
        int n = rand.nextInt(264985521) + 1;
        //rename File
        File fromrenameFile = new File((newPath));
        File torenameFile = new File((GlobalPath + "/" +
                /*path.substring(path.lastIndexOf('/'), path.lastIndexOf('.'))*/
                String.valueOf(n) + ".0"));
        fromrenameFile.renameTo(torenameFile);
        newPath = newPath.substring(0, newPath.lastIndexOf('/')) + "/" + String.valueOf(n) + ".0";
        enc = app.encrypt(newPath);
        values.put("newPath", enc);

        //key
        values.put("key_d", app.getG_key().getEncoded());
        values.put("TimeAndDate", data.getKEY_TIMESTAMP());
        values.put("oldAlbum", data.getKEY_ALBUM());
        values.put("size", data.getKEY_SIZE());
        values.put("res", data.getKEY_RESOLUTION());


        long check = database.insert("Privates", null, values);
        if (check > 0) {
            return true;
        } else {
            return false;
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
        for (int i = album.length() - 1; i >=0; i--) {
            final_album += album.charAt(i);
        }
        Log.d("CSFEWA", "getALbumfromPath: fa:" + final_album);
        return final_album;
    }

    private void AddDataBacktoFragmentsFormPrivate(Photo_Item photo_item) {
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + photo_item.getKEY_SEC_PATH()
                + " o:" + photo_item.getKEY_OLD_PATH());
        String album = getALbumfromPath(photo_item.getKEY_OLD_PATH());
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + album);
        if (album.equals("Camera")) {
            SearchForMainPositionInCamera(photo_item);
        } else {
            SearchForMainPositionInAlbums(album, photo_item);
        }
    }


    private void SearchForMainPositionInAlbums(String album, Photo_Item photo_item) {
        List<PicsWithDates> list = Utils.getAlbumSearch(album);
        assert list != null;
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + photo_item.getKEY_TIMESTAMP().substring(0, 10));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDate().equals(photo_item.getKEY_TIMESTAMP().substring(0, 10))) {
                list.get(i).getPics().add(0, photo_item);
                Constants.isChangeAlbum = true;
                break;
            }
        }
    }

    private void SearchForMainPositionInCamera(Photo_Item photo_item) {
        boolean flag = true;
        Log.d("CSFEWA", "SearchForMainPositionInCamera: " + photo_item.getKEY_TIMESTAMP().substring(0, 10));

        for (int i = 0; i < Constants.Camera_photos_list.size() && flag; i++) {
            Log.d("CSFEWA", "SearchForMainPositionInCamera: " + Constants.Camera_photos_list.get(i).getDate());
            if (Constants.Camera_photos_list.get(i).getDate().equals(photo_item.getKEY_TIMESTAMP().substring(0, 10))) {
                Constants.Camera_photos_list.get(i).getPics().add(0, photo_item);
                Log.d("CSFEWA", "SearchForMainPositionInCamera: Added");
                Constants.isChangeCamera = true;
                break;
            }
        }
    }


    public boolean RemovePrivatephoto(Photo_Item picData, Boolean flag) throws IOException, NoSuchAlgorithmException {
        Photo_Item photo_item = new Photo_Item(
                picData.getKEY_SEC_PATH(),
                picData.getKEY_TIMESTAMP(),
                picData.getKEY_TITLE(),
                picData.getKEY_ALBUM(),
                picData.getKEY_SIZE(),
                picData.getKEY_RESOLUTION(),
                picData.getKEY_TYPE(),
                false);


        //Place on last Location
        if (flag) {
            AddDataBacktoFragmentsFormPrivate(photo_item);
            //move back else delete
            CopyPicture(picData.getKEY_OLD_PATH(), picData.getKEY_SEC_PATH());
            refreshSystemMediaScanDataBase(context, picData.getKEY_SEC_PATH());
        }

        //delete from Media
        File file = new File(picData.getKEY_OLD_PATH());
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
            }
            if (file.exists()) {
                context.deleteFile(file.getName());
            }
        }
        refreshSystemMediaScanDataBase(context, picData.getKEY_OLD_PATH());


        String path2 = null;
        StrongAES app = new StrongAES();
        database = dbHelper.getWritableDatabase();
        Cursor c = database.rawQuery("Select * from Privates", null);
        c.moveToFirst();

        if (c.getCount() > 0) {
            while (!c.isAfterLast()) {
                byte[] oldpathenc = c.getBlob(3);
                byte[] newpathenc = c.getBlob(4);
                byte[] encodedKey = c.getBlob(9);
                SecretKeySpec originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
                String oldPath = app.decrypt(oldpathenc, originalKey);
                String newPath = app.decrypt(newpathenc, originalKey);
                if (oldPath.equals(picData.getKEY_SEC_PATH())) {
                    path2 = newPath;
                }
                c.moveToNext();
            }
        }


        //String Mpath=GlobalPath + picData.getKEY_PATH().substring(picData.getKEY_PATH().lastIndexOf('/'),picData.getKEY_PATH().lastIndexOf('.')) + ".0";
        String Mpath = path2;
        File file2 = new File(Mpath);
        file2.delete();
        if (file2.exists()) {
            try {
                file2.getCanonicalFile().delete();
            } catch (IOException e) {
            }
            if (file2.exists()) {
                context.deleteFile(file2.getName());
            }
        }
        refreshSystemMediaScanDataBase(context, GlobalPath + picData.getKEY_TITLE() + picData.getKEY_TYPE());

        // Delete from database
        database = dbHelper.getWritableDatabase();
        String[] whereArgs = {picData.getKEY_PIC_ID().toString()};
        long check = database.delete("Privates", " Pic_id =?", whereArgs);
        if (check > 0) {
            return true;
        }
        return false;
    }

    public Boolean CopyPicture(String sourcepath, String targetpath) throws IOException {
        File sourceLocation = new File(sourcepath);
        File targetLocation = new File(targetpath);

        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        return true;
    }


    public static void refreshSystemMediaScanDataBase(Context context, String docPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(docPath));
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    public static class Helper extends SQLiteOpenHelper {
        public static String DATABASE_NAME = "GalleryDb";
        public static Integer DATABASE_VERSION = 1;

        public Helper(Context context) {
            super(context,
                    context.getExternalFilesDir(null).getAbsolutePath()
                            + "/" + DATABASE_NAME, null, DATABASE_VERSION);

            File dir = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/Media");
            File dir2 = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/.Temp");
            GlobalPath = context.getExternalFilesDir(null).getAbsolutePath() + "/Media";
            try {
                if (dir.mkdir() && dir2.mkdir()) {
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory is not created");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {


            sqLiteDatabase.execSQL("Create Table IF NOT EXISTS  Favorites(\n" +
                    " Pic_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "path TEXT NOT NULL" + ");");

            sqLiteDatabase.execSQL("Create Table IF NOT EXISTS  Privates(\n" +
                    " Pic_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    " new_name BLOB, " +
                    " type TEXT NOT NULL ," +
                    " oldPath BLOB ," +
                    " newPath BLOB ," +
                    " TimeAndDate Text ," +
                    " oldAlbum Text ," +
                    " size Text ," +
                    " res Text ," +
                    " key_d TEXT NOT NULL " + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("a",
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");

        }
    }

    private List<Photo_Item> makefiles(List<Photo_Item> paths) throws IOException {
        List<Photo_Item> final_Data = new ArrayList<>();
        if (paths.size() > 0) {
            for (int i = 0; i < paths.size(); i++) {
                String fromPath = paths.get(i).getKEY_SEC_PATH();
                Log.d("ggF", "makefiles: " + paths.get(i).getKEY_SEC_PATH());

            /*     //rename File
                File fromrenameFile = new File((makefileAt));
                if (fromrenameFile.exists()) {
                    File torenameFile = new File(fromPath);
                    torenameFile.renameTo(fromrenameFile);
                }*/


                File file = new File(fromPath);
                byte[] data = convertFileToByteArray(context, file);

                File myFile = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/" + ".Temp/", paths.get(i).getKEY_TITLE()
                        + paths.get(i).getKEY_TYPE());

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(myFile);
                    out.write(data);
                    out.flush();

                    Log.d("ggF", "makefiles: " + myFile.getPath());
                    Photo_Item picData = new Photo_Item(
                            paths.get(i).getKEY_PIC_ID(),
                            myFile.getPath(),
                            paths.get(i).getKEY_OLD_PATH(),
                            paths.get(i).getKEY_TIMESTAMP(),
                            paths.get(i).getKEY_TITLE(),
                            paths.get(i).getKEY_ALBUM(),
                            paths.get(i).getKEY_RESOLUTION(),
                            paths.get(i).getKEY_SIZE(),
                            paths.get(i).getKEY_TYPE());
                    final_Data.add(picData);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return final_Data;
    }

    public List<Photo_Item> getPrivatePics() throws NoSuchAlgorithmException, IOException {
        StrongAES app = new StrongAES();

        List<Photo_Item> list = new ArrayList<>();
        database = dbHelper.getWritableDatabase();
        Cursor c = database.rawQuery("Select * from Privates", null);
        c.moveToFirst();

        if (c.getCount() > 0) {
            while (!c.isAfterLast()) {
                Integer id = c.getInt(0);
                byte[] new_nameenc = c.getBlob(1);
                String type = c.getString(2);
                byte[] oldpathenc = c.getBlob(3);
                ;
                byte[] newpathenc = c.getBlob(4);
                ;
                String album = "Private";
                String time = c.getString(5);
                String size = c.getString(7);
                String res = c.getString(8);

                byte[] encodedKey = c.getBlob(9);
                SecretKeySpec originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
                String newName = app.decrypt(new_nameenc, originalKey);
                String oldPath = app.decrypt(oldpathenc, originalKey);
                String newPath = app.decrypt(newpathenc, originalKey);

                Photo_Item p = new Photo_Item(id, oldPath, newPath, time, newName, album, res, size, type);

                if (size.charAt(0) != '0') {
                    list.add(p);
                }
                c.moveToNext();
            }
        }
        return makefiles(list);
    }

    public static class StrongAES {
        public SecretKeySpec getG_key() {
            return g_key;
        }

        SecretKeySpec g_key;

        public StrongAES() throws NoSuchAlgorithmException {
            g_key = generateKey();
        }

        public byte[] encrypt(String Path) {
            try {
                Key aesKey = g_key;
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encrypted = cipher.doFinal(Path.getBytes());
                return encrypted;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String decrypt(byte[] encrypted, SecretKeySpec key) {
            try {
                // Create key and cipher
                Key aesKey = key;
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                String decrypted = new String(cipher.doFinal(encrypted));
                return decrypted;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        SecretKeySpec generateKey() throws NoSuchAlgorithmException {
            KeyGenerator keygen = KeyGenerator.getInstance("AES"); // key generator to be used with AES algorithm.
            keygen.init(256); // Key size is specified here.
            byte[] key = keygen.generateKey().getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            return skeySpec;
        }

    }


    private static byte[] convertFileToByteArray(Context context, File file) {
        byte[] byteArray = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 8];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArray;
    }
}
