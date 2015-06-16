package com.hmkcode.android.adapters.lists;

public class PPlist {
    private String pubname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public PPlist(String pubname,String id) {
        super();
        this.pubname = pubname;
        this.id = id;
    }

    public String getPubname() {
        return pubname;
    }

    public void setPubname(String pubname) {
        this.pubname = pubname;
    }


}