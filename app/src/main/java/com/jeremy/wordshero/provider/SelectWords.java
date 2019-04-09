package com.jeremy.wordshero.provider;


import java.io.Serializable;

/**
 * 选择单词页面的临时类
 */
public class SelectWords implements Serializable {
    private static final long serializableUID = 1L;
    public String words;

    public SelectWords(){}
    public SelectWords(String words) {
        this.words  = words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getWords() {
        return words;
    }

    @Override
    public String toString() {
        return "SelectWords [id=" + words + "]";
    }


}
