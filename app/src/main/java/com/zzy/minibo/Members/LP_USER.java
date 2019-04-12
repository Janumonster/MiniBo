package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_USER extends LitePalSupport {

    private String uidstr;

    private String screen_name;

    private String json;

    public String getUidstr() {
        return uidstr;
    }

    public void setUidstr(String uidstr) {
        this.uidstr = uidstr;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
