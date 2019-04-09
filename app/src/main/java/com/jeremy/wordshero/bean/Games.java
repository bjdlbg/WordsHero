package com.jeremy.wordshero.bean;

import android.view.View;

/**
 * @author lxh
 */
public class Games {
    private String title;
    private String instruction;

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

    public Games(String mTitle, String mInstruction) {
        title=mTitle;
        instruction=mInstruction;
    }
    /**编辑游戏接口*/
    public interface ItemEditBtnClickListener{
        void onItemEditBtnClick(int position);
    }
    /**查看答案接口*/
    public interface ItemAnswerTvClickListener{
        void onItemAnswerTvClick(int position, View view,String s);
    }
    /**点击item预览pdf*/
    public interface ItemShowPdfListener{
        void onItemClick(int position);
    }
    /**
     * 长按删除游戏
     */
    public interface ItemDeleteGameListener{
        void onItemLongClick(int position);
    }
}