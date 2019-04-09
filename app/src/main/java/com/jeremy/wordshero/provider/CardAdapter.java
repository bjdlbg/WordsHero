package com.jeremy.wordshero.provider;

import android.support.v7.widget.CardView;

/**
 * @author: J.xiang
 * @date: On 2018/11/13
 */
public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
