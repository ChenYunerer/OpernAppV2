package com.yun.opernv2.net.request;

public class AddCollectionReq {
    private long userId;
    private int opernId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getOpernId() {
        return opernId;
    }

    public void setOpernId(int opernId) {
        this.opernId = opernId;
    }
}
