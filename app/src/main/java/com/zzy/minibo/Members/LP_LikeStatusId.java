package com.zzy.minibo.Members;

import org.litepal.crud.LitePalSupport;

public class LP_LikeStatusId extends LitePalSupport {

    private String status_id;
    private int like_num;

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }
}
