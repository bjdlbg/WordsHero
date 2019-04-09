package com.jeremy.wordshero.bean;

import java.io.Serializable;

public class WordInfo implements Serializable {
    private static final long serializableUID = 2L;
    private int id;
    private String word;
    private String translation;
    private int book_id;
    private long crate_time;
    private long update_time;
    private boolean isShow;//是否显示CheckBox
    private boolean isChecked;//是否选中CheckBox
    private boolean haveShowTranslation = false;//是否显示有翻译，初始化时不显示翻译

    public boolean isHaveShowTranslation() {
        return haveShowTranslation;
    }

    public void setHaveShowTranslation(boolean haveShowTranslation) {
        this.haveShowTranslation = haveShowTranslation;
    }



    public WordInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public long getCrate_time() {
        return crate_time;
    }

    public void setCrate_time(long crate_time) {
        this.crate_time = crate_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }


    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


    public WordInfo(String mEnglish, String mTranslation) {
        this.word = mEnglish;
        this.translation=mTranslation;
    }


}

