package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_STATUS extends LitePalSupport {

    private String idstr;
    private String json;

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
