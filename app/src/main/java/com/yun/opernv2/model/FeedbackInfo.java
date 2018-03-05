package com.yun.opernv2.model;

public class FeedbackInfo {
    private long userId;
    private String feedbackMessage;
    private String feedbackDateTime;
    private String communicateFlg;
    private String delFlg;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackDateTime() {
        return feedbackDateTime;
    }

    public void setFeedbackDateTime(String feedbackDateTime) {
        this.feedbackDateTime = feedbackDateTime;
    }

    public String getCommunicateFlg() {
        return communicateFlg;
    }

    public void setCommunicateFlg(String communicateFlg) {
        this.communicateFlg = communicateFlg;
    }

    public String getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg;
    }
}
