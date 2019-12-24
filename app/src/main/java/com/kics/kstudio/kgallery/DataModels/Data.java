package com.kics.kstudio.kgallery.DataModels;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private String album;
    private List<PicsWithDates> album_photos_list;

    public String getAlbum() {
        return album;
    }

    public List<PicsWithDates> getAlbum_photos_list() {
        return album_photos_list;
    }

    public Data(String album, List<PicsWithDates> album_photos_list) {
        this.album = album;
        this.album_photos_list = album_photos_list;
    }


}
