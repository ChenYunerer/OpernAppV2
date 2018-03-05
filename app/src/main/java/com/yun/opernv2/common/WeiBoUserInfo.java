package com.yun.opernv2.common;

/**
 * Created by Yun on 2017/8/29 0029.
 */

public class WeiBoUserInfo {

    private long id;
    private String name;
    private String location;
    private String description;
    private String cover_image_phone;
    private String gender;
    private String avatar_hd;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover_image_phone() {
        return cover_image_phone;
    }

    public void setCover_image_phone(String cover_image_phone) {
        this.cover_image_phone = cover_image_phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar_hd() {
        return avatar_hd;
    }

    public void setAvatar_hd(String avatar_hd) {
        this.avatar_hd = avatar_hd;
    }

    @Override
    public String toString() {
        return "WeiBoUserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", cover_image_phone='" + cover_image_phone + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar_hd='" + avatar_hd + '\'' +
                '}';
    }
}
