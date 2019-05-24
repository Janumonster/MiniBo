package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_RepostCount extends LitePalSupport {

    private String status_id;
    private int repost_count;

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public int getRepost_count() {
        return repost_count;
    }

    public void setRepost_count(int repost_count) {
        this.repost_count = repost_count;
    }
}
