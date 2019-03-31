package com.jeremy.wordshero.activity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.jeremy.wordshero.R;

import com.jeremy.wordshero.fragment.AboutFragment;
import com.jeremy.wordshero.fragment.ChartFragment;
import com.jeremy.wordshero.fragment.MessageFragment;
import com.jeremy.wordshero.fragment.WordFragment;

/**
 * 程序入口
 * @author jixiang
 * */
public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //底部导航点击事件监听
        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onTabSelected(item.getItemId());
                return true;
            }
        };
        //设置监听
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                Log.e("jeremy debug","Reselect item"+menuItem.getTitle());
            }
        });
        onTabSelected(R.id.navigation_words);
    }

    /***
     * navigation事件
     * @param id
     */
    private void onTabSelected(int id){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = null;
        switch (id){
            case R.id.navigation_words:
                fragment = new WordFragment();
                break;
            case R.id.navigation_chart:
                fragment = new ChartFragment();
                break;
            case R.id.navigation_message:
                fragment = new MessageFragment();
                break;
            case R.id.navigation_about:
                fragment = new AboutFragment();
                break;
                default:break;
        }
        transaction.replace(R.id.container,fragment);
        transaction.commit();
    }


}
