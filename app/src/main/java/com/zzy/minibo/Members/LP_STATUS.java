package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_STATUS extends LitePalSupport {

    private String idstr;
    private String json;
    private boolean isLocal = false;
    private int repost_count = 0;
    private int coment_count = 0;
    private int like_count = 0;

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
    public int getRepost_count() {
        return repost_count;
    }

    public void setRepost_count(int repost_count) {
        this.repost_count = repost_count;
    }

    public int getComent_count() {
        return coment_count;
    }

    public void setComent_count(int coment_count) {
        this.coment_count = coment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }



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
