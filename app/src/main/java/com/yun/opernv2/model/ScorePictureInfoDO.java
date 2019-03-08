package com.yun.opernv2.model;


import java.io.Serializable;

/**
 * 曲谱图片信息
 */
public class ScorePictureInfoDO implements Serializable {
    private int id;  //主键id
    private int scoreId;  //曲谱id
    private String scoreName;  //曲谱名称
    private String scoreHref;  //曲谱地址
    private int scorePictureIndex;  //曲谱图片index
    private String scorePictureHref;  //曲谱图片地址

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
    }

    public String getScoreHref() {
        return scoreHref;
    }

    public void setScoreHref(String scoreHref) {
        this.scoreHref = scoreHref;
    }

    public int getScorePictureIndex() {
        return scorePictureIndex;
    }

    public void setScorePictureIndex(int scorePictureIndex) {
        this.scorePictureIndex = scorePictureIndex;
    }

    public String getScorePictureHref() {
        return scorePictureHref;
    }

    public void setScorePictureHref(String scorePictureHref) {
        this.scorePictureHref = scorePictureHref;
    }
}

