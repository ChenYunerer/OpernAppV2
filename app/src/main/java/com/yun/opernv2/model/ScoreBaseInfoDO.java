package com.yun.opernv2.model;

import java.io.Serializable;

/**
 * 曲谱基本信息
 */
public class ScoreBaseInfoDO implements Serializable {
    private int id;  //主键id
    private int scoreId;  //曲谱id
    private String scoreCategory;  //曲谱类型
    private String scoreName; //曲谱名称
    private String scoreHref;  //曲谱地址
    private String scoreSinger;  //演唱者
    private String scoreAuthor;  //作者
    private String scoreWordWriter;  //词作者
    private String scoreSongWriter;  //曲作者
    private String scoreFormat;  //曲谱格式
    private String scoreOrigin;  //曲谱源
    private String scoreUploader;  //上传者
    private long scoreUploaderTime;  //上传时间
    private int scoreViewCount;  //访问量
    private String scoreCoverPicture;  //曲谱首页图
    private int scorePictureCount;  //曲谱图数量

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

    public String getScoreCategory() {
        return scoreCategory;
    }

    public void setScoreCategory(String scoreCategory) {
        this.scoreCategory = scoreCategory;
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

    public String getScoreSinger() {
        return scoreSinger;
    }

    public void setScoreSinger(String scoreSinger) {
        this.scoreSinger = scoreSinger;
    }

    public String getScoreAuthor() {
        return scoreAuthor;
    }

    public void setScoreAuthor(String scoreAuthor) {
        this.scoreAuthor = scoreAuthor;
    }

    public String getScoreWordWriter() {
        return scoreWordWriter;
    }

    public void setScoreWordWriter(String scoreWordWriter) {
        this.scoreWordWriter = scoreWordWriter;
    }

    public String getScoreSongWriter() {
        return scoreSongWriter;
    }

    public void setScoreSongWriter(String scoreSongWriter) {
        this.scoreSongWriter = scoreSongWriter;
    }

    public String getScoreFormat() {
        return scoreFormat;
    }

    public void setScoreFormat(String scoreFormat) {
        this.scoreFormat = scoreFormat;
    }

    public String getScoreOrigin() {
        return scoreOrigin;
    }

    public void setScoreOrigin(String scoreOrigin) {
        this.scoreOrigin = scoreOrigin;
    }

    public String getScoreUploader() {
        return scoreUploader;
    }

    public void setScoreUploader(String scoreUploader) {
        this.scoreUploader = scoreUploader;
    }

    public long getScoreUploaderTime() {
        return scoreUploaderTime;
    }

    public void setScoreUploaderTime(long scoreUploaderTime) {
        this.scoreUploaderTime = scoreUploaderTime;
    }

    public int getScoreViewCount() {
        return scoreViewCount;
    }

    public void setScoreViewCount(int scoreViewCount) {
        this.scoreViewCount = scoreViewCount;
    }

    public String getScoreCoverPicture() {
        return scoreCoverPicture;
    }

    public void setScoreCoverPicture(String scoreCoverPicture) {
        this.scoreCoverPicture = scoreCoverPicture;
    }

    public int getScorePictureCount() {
        return scorePictureCount;
    }

    public void setScorePictureCount(int scorePictureCount) {
        this.scorePictureCount = scorePictureCount;
    }
}
