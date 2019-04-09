package com.jeremy.wordshero.bean;

import android.media.Image;

/**
 * 为card显示所需要的资源构造方法
 * @author: J.xiang
 * @date: On 2018/11/13
 */
public class CardItem {

    private String mTextResource;
    private String mTitleResource;
    private Image mBackgroundImage;

    //带有图片
    public CardItem(String title, String text, Image image) {
        mTitleResource = title;
        mTextResource = text;
        mBackgroundImage = image;
    }

    public CardItem(String title, String text) {
        mTitleResource = title;
        mTextResource = text;
    }


    public String getText() {
        return mTextResource;
    }

    public String getTitle() {
        return mTitleResource;
    }

    public Image getImage() {
        return mBackgroundImage;
    }
}
