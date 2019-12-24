package com.kics.kstudio.kgallery.DataModels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.Serializable;

public class Photo_Item implements Serializable {


    private Integer KEY_PIC_ID;
    private Integer KEY_MAIN_POS;
    private String KEY_SEC_PATH;

    private String KEY_OLD_PATH;
    private String KEY_TIMESTAMP;
    private String KEY_TITLE;
    private String KEY_ALBUM;
    private String KEY_SIZE;
    private String KEY_RESOLUTION;
    private String KEY_TYPE;
    private Boolean KEY_FVR8;
    private File image;

    public Photo_Item(String KEY_OLD_PATH,
                      String KEY_TIMESTAMP,
                      String KEY_TITLE,
                      String KEY_ALBUM,
                      String KEY_SIZE,
                      String KEY_RESOLUTION,
                      String KEY_TYPE,
                      Boolean KEY_FVR8) {
        this.KEY_OLD_PATH = KEY_OLD_PATH;
        this.KEY_TIMESTAMP = KEY_TIMESTAMP;
        this.KEY_TITLE = KEY_TITLE;
        this.KEY_ALBUM = KEY_ALBUM;
        this.KEY_SIZE = KEY_SIZE;
        this.KEY_RESOLUTION = KEY_RESOLUTION;
        this.KEY_TYPE = KEY_TYPE;
        this.KEY_FVR8 = KEY_FVR8;
        image = new File(KEY_OLD_PATH);
    }

    public Photo_Item(Integer KEY_PIC_ID,
                      String KEY_OLD_PATH,
                      String KEY_SEC_PATH,
                      String KEY_TIMESTAMP,
                      String KEY_TITLE,
                      String KEY_ALBUM,
                      String KEY_RESOLUTION,
                      String KEY_SIZE,
                      String KEY_TYPE) {

        this.KEY_PIC_ID = KEY_PIC_ID;
        this.KEY_OLD_PATH = KEY_OLD_PATH;
        this.KEY_TIMESTAMP = KEY_TIMESTAMP;
        this.KEY_TITLE = KEY_TITLE;
        this.KEY_ALBUM = KEY_ALBUM;
        this.KEY_SIZE = KEY_SIZE;
        this.KEY_RESOLUTION = KEY_RESOLUTION;
        this.KEY_TYPE = KEY_TYPE;
        this.KEY_SEC_PATH = KEY_SEC_PATH;
        image = new File(KEY_SEC_PATH);

    }

    public Photo_Item(String key_old_path, String key_timestamp) {
        this.KEY_OLD_PATH=key_old_path;
        this.KEY_TIMESTAMP=key_timestamp;
    }


    public String getKEY_OLD_PATH() {
        return KEY_OLD_PATH;
    }

    public String getKEY_TIMESTAMP() {
        return KEY_TIMESTAMP;
    }

    public String getKEY_TITLE() {
        return KEY_TITLE;
    }

    public String getKEY_ALBUM() {
        return KEY_ALBUM;
    }

    public String getKEY_SIZE() {
        return KEY_SIZE;
    }

    public String getKEY_RESOLUTION() {
        return KEY_RESOLUTION;
    }

    public Boolean getKEY_FVR8() {
        return KEY_FVR8;
    }

    public File getImage() {
        return image;
    }

    public String getKEY_TYPE() {
        return KEY_TYPE;
    }

    public String getKEY_SEC_PATH() {
        return KEY_SEC_PATH;
    }

    public Integer getKEY_PIC_ID() {
        return KEY_PIC_ID;
    }

    public Integer getKEY_MAIN_POS() {
        return KEY_MAIN_POS;
    }

    public void setKEY_MAIN_POS(Integer KEY_MAIN_POS) {
        this.KEY_MAIN_POS = KEY_MAIN_POS;
    }
}
