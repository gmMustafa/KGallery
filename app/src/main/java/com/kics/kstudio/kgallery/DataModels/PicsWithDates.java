package com.kics.kstudio.kgallery.DataModels;

import com.kics.kstudio.kgallery.DataModels.Photo_Item;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HP on 8/15/2018.
 */

public class PicsWithDates implements Serializable {
    private String date;
    private List<Photo_Item> Pics;


    public PicsWithDates(String date, List<Photo_Item> pics) {
        this.date = date;
        Pics = pics;
    }

    public PicsWithDates() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Photo_Item> getPics() {
        return Pics;
    }

    public void setPics(List<Photo_Item> list) {
        this.Pics=list;
    }
}
