package com.jeremy.wordshero.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.fragment.tab.TakeWordFragment;
import com.jeremy.wordshero.fragment.tab.WordBooksFragment;
import com.jeremy.wordshero.fragment.tab.WordsGameFragment;

import java.util.Objects;

/**
 * @author jixiang
 * @date 2019/3/25
 */
public class WordFragment extends Fragment{

    public View rootView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    /**tab导航三个page*/
    private TakeWordFragment mTakeWordFragment=new TakeWordFragment();
    private WordBooksFragment mWordBooksFragment=new WordBooksFragment();
    private WordsGameFragment mWordsGameFragment=new WordsGameFragment();

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
        //注册监听
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //当tab中item被选中时候触发
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mTabLayout.getTabAt(i).select();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //添加适配器
        mViewPager.setAdapter(new FragmentPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return mTakeWordFragment;
                    case 1:
                        return mWordsGameFragment;
                    case 2:
                        return mWordBooksFragment;
                    default:break;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        return rootView;
    }

    private void initView(View rootView){
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);


    }

}
