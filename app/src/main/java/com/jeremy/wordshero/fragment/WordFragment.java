package com.jeremy.wordshero.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremy.wordshero.R;

/**
 * @author jixiang
 * @date 2019/3/25
 */
public class WordFragment extends Fragment {

    public View rootView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        initView(rootView);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_words,container,false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView){
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
//        tabLayout.addTab(tabLayout.newTab().setText("C"));
//        tabLayout.addTab(tabLayout.newTab().setText("C++"));
//        tabLayout.addTab(tabLayout.newTab().setText("Java"));

    }

}
