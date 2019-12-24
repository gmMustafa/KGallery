package com.kics.kstudio.kgallery.Misc;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.kics.kstudio.kgallery.DataModels.Data;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {
    public static final int REQUEST_CODE_TAKE_IMAGE = 121;
    public static final int REQUEST_PERMISSION_KEY = 1;
    public static final int SPLASH_TIME = 1000;
    public static final String KEY_ALBUM = "album_name";
    public static final String KEY_PATH = "path";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_TIME = "date";
    public static final String KEY_COUNT = "date";
    public static final String KEY_SIZE = "size";
    public static final String KEY_RESOLUTION = "resolution";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type";
    public static final String KEY_FVR8 = "check";
    public static boolean DeleteAlso =false ;


    public static Integer span_Count = 4;
    public static Context main_context;
    public static Boolean selection = false;
    public static String Fragment_name = "";

    public static Integer counter = 0;
    public static Boolean pass = false;
    public static boolean change_in_favr8_fragment = false;
    public static boolean change_in_albums = false;
    public static boolean change_in_photo_fragment = false;

    public static boolean single_album_pass = false;
    public static boolean is_private = false;
    public static boolean is_timetoChangeTb = false;
    public static boolean passwordNalagana=false;
    public static boolean removeOnly_removePrivae = false;
    //true removePrivate false removeOnly
    public static boolean remove_orRemoveandPrivatize=false;
    //true removeandPrivate false removeOnly


    public static KProgressHUD kProgressHUD;
    public static LovelyStandardDialog lovelyStandardDialog;
    public static Bundle bundle;

    public static List<Data> all_album_photos_list = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> albumList= new ArrayList<>();
    public static List<PicsWithDates> Current_album_photos_list= new ArrayList<>();
    public static List<PicsWithDates> Camera_photos_list= new ArrayList<>();

    public static ArrayList<Positions> positions = new ArrayList<>();
    public static ArrayList<Positions> Tempositions = new ArrayList<>();


    public static List<Photo_Item> private_list= new ArrayList<>();
    public static List<Photo_Item> favr8_list=new ArrayList<>();

    public static List<Photo_Item> Selected_list = new ArrayList<>();
    public static List<Photo_Item> TempSelected_list = new ArrayList<>();

    public static List<Photo_Item> RemovePositionsFromVP =new ArrayList<>();
    public static List<Photo_Item> AddFavr8=new ArrayList<Photo_Item>();
    public static List<Photo_Item> PrivatesToRemove=new ArrayList<Photo_Item>();

    public static List<String> paths=new ArrayList<>();

    public static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
//            Manifest.permission.READ_CALL_LOG,
//            Manifest.permission.WRITE_CALL_LOG,
//            Manifest.permission.READ_PHONE_STATE
//            Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE,

    };



    public static boolean isChangeCamera=false;
    public static boolean isChangeAlbum=false;
    public static boolean changeinPrivate=false;
    public static boolean deletTempPrivates=false;
    public static boolean back=false;


}
