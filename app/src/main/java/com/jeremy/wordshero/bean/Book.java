package com.jeremy.wordshero.bean;

import java.io.Serializable;

/**
 * @author lxh
 */
public class Book implements Serializable {
    private int id;
    private String name;
    private String introduction;
    private int num;
    private long createTime;
    private long updateTime;
    private boolean isChecked;
    private int position;
    private boolean checkBoxIsShow = false;


    public boolean isCheckBoxIsShow() {
        return checkBoxIsShow;
    }

    public void setCheckBoxIsShow(boolean checkBoxIsShow) {
        this.checkBoxIsShow = checkBoxIsShow;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Book(String name, String introduction) {
        this.name = name;
        this.introduction = introduction;
    }

    public Book(String bookName, String introduction, int wordCount, long createTime) {
        this.name = bookName;
        this.introduction = introduction;
        this.num = wordCount;
        this.createTime = createTime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Book() {
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
