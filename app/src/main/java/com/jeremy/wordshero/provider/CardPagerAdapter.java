package com.jeremy.wordshero.provider;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.CardItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: J.xiang
 * @date: On 2018/11/13
 */
public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    //存放view与item
    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }
    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view=LayoutInflater.from(container.getContext())
                .inflate(R.layout.pager_adapter,container,false);
        container.addView(view);
        bind(mData.get(position),view);
        CardView cardView=(CardView)view.findViewById(R.id.cardView);

        if (mBaseElevation==0){
            mBaseElevation=cardView.getCardElevation() ;
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);;
        mViews.set(position,cardView);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }
    //用于添加一个item包括里面view中的信息
    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }

}
