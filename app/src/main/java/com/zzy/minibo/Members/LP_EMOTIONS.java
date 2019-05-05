package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_EMOTIONS extends LitePalSupport {

    private String value;

    private String path;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
