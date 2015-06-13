package com.hmkcode.android.gcm;

/**
 * Created by mahantesh on 6/5/2015.
 */
public class user {
    private String name;
    private String Pid;
    public String getPid() {
        return Pid;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public user(String Pid, String name) {
        this.Pid = Pid;
        this.name = name;
    }

}