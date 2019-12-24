package com.kics.kstudio.kgallery.DataModels;

/**
 * Created by HP on 8/17/2018.
 */

public class PicDetailsInfo {

    String title;

    public PicDetailsInfo(String s, String s1) {
        this.title = s;
        this.Details = s1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    String Details;

}
