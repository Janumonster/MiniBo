package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_COMMENTS extends LitePalSupport {


    private String idstr;
    private String status_id;
    private String user_id;
    private String json;

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
