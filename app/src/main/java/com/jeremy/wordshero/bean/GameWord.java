package com.jeremy.wordshero.bean;

/**
 * @author lxh
 */
public class GameWord {

    private int id;
    private String word;
    private String clue;
    private int gameId;
    private String book;
    private boolean isChecked=true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public GameWord(String word, String clue, String book){
        this.word=word;
        this.clue = clue;
        this.book=book;
    }
    public GameWord(String word,String clue,int gameId,String book){
        this.word=word;
        this.clue=clue;
        this.gameId=gameId;
        this.book=book;
    }
    public GameWord(){};
}
