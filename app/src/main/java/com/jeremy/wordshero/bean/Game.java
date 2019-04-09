package com.jeremy.wordshero.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author lxh
 */
public class Game implements Parcelable{
    private int id;
    private String title;
    private String instruction;
    private int count;
    private long createTime;
    private long updateTime;
    private long openTime;
    private String pdfPath;
    private boolean checkBoxIsShow ;
    private boolean checkBoxIsSelected ;
    private int position;

    protected Game(Parcel in) {
        id = in.readInt();
        title = in.readString();
        instruction = in.readString();
        count = in.readInt();
        createTime = in.readLong();
        updateTime = in.readLong();
        openTime = in.readLong();
        pdfPath = in.readString();
        checkBoxIsShow = in.readByte() != 0;
        checkBoxIsSelected = in.readByte() != 0;
        position = in.readInt();
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            Game game=new Game();
            game.id = in.readInt();
            game.title = in.readString();
            game.instruction = in.readString();
            game.count = in.readInt();
            game.createTime = in.readLong();
            game.updateTime = in.readLong();
            game.openTime = in.readLong();
            game.pdfPath = in.readString();
            game.checkBoxIsShow = in.readByte() != 0;
            game.checkBoxIsSelected = in.readByte() != 0;
            game.position = in.readInt();
            return game;
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public boolean isCheckBoxIsShow() {
        return checkBoxIsShow;
    }

    public void setCheckBoxIsShow(boolean checkBoxIsShow) {
        this.checkBoxIsShow = checkBoxIsShow;
    }

    public boolean isCheckBoxIsSelected() {
        return checkBoxIsSelected;
    }

    public void setCheckBoxIsSelected(boolean checkBoxIsSelected) {
        this.checkBoxIsSelected = checkBoxIsSelected;
    }


    public Game() {
    }

    /**
     * 测试用
     *
     * @param mTitle
     * @param mInstruction
     * @param mCount
     * @param mCreate_time
     * @param mUpdate_time
     * @param mOpen_time
     */
    public Game(String mTitle, String mInstruction, int mCount, long mCreate_time, long mUpdate_time, long mOpen_time, String mPDF_path) {
        title = mTitle;
        instruction = mInstruction;
        count = mCount;
        createTime = mCreate_time;
        updateTime = mUpdate_time;
        openTime = mOpen_time;
        pdfPath = mPDF_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Game(String mTitle, String mInstruction) {
        title = mTitle;
        instruction = mInstruction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(instruction);
        dest.writeInt(count);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeLong(openTime);
        dest.writeString(pdfPath);
        dest.writeByte((byte) (checkBoxIsShow ? 1 : 0));
        dest.writeByte((byte) (checkBoxIsSelected ? 1 : 0));
        dest.writeInt(position);
    }

}
