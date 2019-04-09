package com.jeremy.wordshero.provider;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;

/**
 * 未调用（选择单词卡片时候会有一个浮动效果）
 * @author: J.xiang
 * @date: On 2018/11/14
 */
public class ShadowTransformer implements ViewPager.PageTransformer , ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private CardAdapter mCardAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;

    public ShadowTransformer(ViewPager viewPager, CardAdapter adapter){
        mViewPager=viewPager;
        viewPager.addOnPageChangeListener(this);
        mCardAdapter=adapter;
    }
    public void enableScaling(boolean enable){
        if (mScalingEnabled && !enable){
            CardView currentCard=mCardAdapter.getCardViewAt(mViewPager.
            getCurrentItem());
            if (currentCard !=null){
                currentCard.animate().scaleX(1.1f);
                currentCard.animate().scaleY(1.1f);
            }
        }
        mScalingEnabled=enable;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation =mCardAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft=mLastOffset > positionOffset;
        if (goingLeft){
            realCurrentPosition=position+1;
            nextPosition=position;
            realOffset=1-positionOffset;
        }else {
            nextPosition=position+1;
            realCurrentPosition=position;
            realOffset=positionOffset;
        }

        if(nextPosition > mCardAdapter.getCount()-1||realCurrentPosition>mCardAdapter.getCount()-1){
            return;
        }

        CardView currentCard=mCardAdapter.getCardViewAt(realCurrentPosition);

        if (currentCard!=null){
            if (mScalingEnabled){
                currentCard.setScaleX((float)(1+0.1*(1-realOffset)));
                currentCard.setScaleY((float)(1+0.1*(1-realOffset)));
            }
            currentCard.setCardElevation((baseElevation+baseElevation*
                    (CardAdapter.MAX_ELEVATION_FACTOR-1)*(1-realOffset)));
        }
        CardView nextCard=mCardAdapter.getCardViewAt(nextPosition);
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }
        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void transformPage(View page, float position) {

    }
}
