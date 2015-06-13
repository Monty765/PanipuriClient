package com.hmkcode.android.gcm;

/**
 * Created by mahantesh on 6/12/2015.
 */
public class Weather {
    public int icon;
    public String title;
    public Weather(){
        super();
    }

    public Weather(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }
}