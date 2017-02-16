package com.example.dell.vo;

/**
 * Created by Dell on 2017/2/9.
 */

public class ShowVO {
    private String mAddress;
    private String mIp;
    private String mPost;
    private String mPassword;
    private String mEncryption;
    private String mName;
    private String mImg;
    private int mCountry;

    public ShowVO(String mAddress, String mIp, String mPost, String mPassword, String mEncryption, String mName, String mImg, int mCountry) {
        this.mAddress = mAddress;
        this.mIp = mIp;
        this.mPost = mPost;
        this.mPassword = mPassword;
        this.mEncryption = mEncryption;
        this.mName = mName;
        this.mImg = mImg;
        this.mCountry = mCountry;
    }

    public int getmCountry() {
        return mCountry;
    }

    public void setmCountry(int mCountry) {
        this.mCountry = mCountry;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmIp() {
        return mIp;
    }

    public void setmIp(String mIp) {
        this.mIp = mIp;
    }

    public String getmPost() {
        return mPost;
    }

    public void setmPost(String mPost) {
        this.mPost = mPost;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmEncryption() {
        return mEncryption;
    }

    public void setmEncryption(String mEncryption) {
        this.mEncryption = mEncryption;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImg() {
        return mImg;
    }

    public void setmImg(String mImg) {
        this.mImg = mImg;
    }
}
