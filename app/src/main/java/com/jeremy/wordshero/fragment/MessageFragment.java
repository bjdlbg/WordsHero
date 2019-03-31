package com.jeremy.wordshero.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.activity.MainActivity;

/**
 * @author jixiang
 * @date 2019/3/25
 */
public class MessageFragment extends Fragment {

    private View rootView;
    private MainActivity mMainActivity;
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_message,container,false);
        return rootView;
    }
    /**
     * 当fragment与activity建立联系时执行此函数
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity= (MainActivity) context;

    }
}
