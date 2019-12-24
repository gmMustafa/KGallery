package com.kics.kstudio.kgallery.DataModels;

/**
 * Created by HP on 7/30/2018.
 */

public class MapMarkers {


    private Integer FIELD_ROW_ID;
    private Double FIELD_LAT;
    private Double FIELD_LNG;
    private String FIELD_TITLE;

    public MapMarkers() {

    }


    public MapMarkers(Integer FIELD_ROW_ID, Double FIELD_LAT, Double FIELD_LNG, String FIELD_TITLE) {
        this.FIELD_ROW_ID = FIELD_ROW_ID;
        this.FIELD_LAT = FIELD_LAT;
        this.FIELD_LNG = FIELD_LNG;
        this.FIELD_TITLE = FIELD_TITLE;
    }

    public Integer getFIELD_ROW_ID() {
        return FIELD_ROW_ID;
    }

    public void setFIELD_ROW_ID(Integer FIELD_ROW_ID) {
        this.FIELD_ROW_ID = FIELD_ROW_ID;
    }

    public Double getFIELD_LAT() {
        return FIELD_LAT;
    }

    public void setFIELD_LAT(Double FIELD_LAT) {
        this.FIELD_LAT = FIELD_LAT;
    }

    public Double getFIELD_LNG() {
        return FIELD_LNG;
    }

    public void setFIELD_LNG(Double FIELD_LNG) {
        this.FIELD_LNG = FIELD_LNG;
    }

    public String getFIELD_TITLE() {
        return FIELD_TITLE;
    }

    public void setFIELD_TITLE(String FIELD_TITLE) {
        this.FIELD_TITLE = FIELD_TITLE;
    }
}
