package com.jeremy.wordshero.provider;

import android.view.View;

/**
 * @author lxh
 * RecyclerView一系列点击事件和滑动事件的接口用于对RecyclerView中的数据进行操作
 */
public interface ItemClickListener {
    /**
     * Item被右滑的时候调用
     * @param position item的位置
     * @param view 触发事件的View
     */
//    void onItemSlideRight(int position,View view);

    /**
     * Item左滑的时候调用
     * @param position item位置
     * @param view 触发事件的View
     */
    void onItemSlideLeft(int position, View view);

    /**
     * item单击事件
     * @param position item的位置
     * @param v view
     */
    void onItemClicked(int position, View v);

    /**
     * item长按事件
     * @param position item的位置
     * @param v view
     * @return
     */
    boolean onItemLongClicked(int position, View v);
}
