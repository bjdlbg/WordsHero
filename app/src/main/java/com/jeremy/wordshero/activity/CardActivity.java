package com.jeremy.wordshero.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.CardItem;
import com.jeremy.wordshero.bean.WordInfo;
import com.jeremy.wordshero.provider.CardPagerAdapter;
import com.jeremy.wordshero.provider.CrosswordsDaoImp;
import com.jeremy.wordshero.provider.ShadowTransformer;


import java.util.ArrayList;
import java.util.List;

/**
 * @author J xiang
 */
public class CardActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    public List<WordInfo> wordsInfoList = new ArrayList<>();
    private ViewPager mViewPager;
    private CheckBox mCheckBox;
    private Button convertToPDF;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CrosswordsDaoImp crosswordsDaoImp;
    private int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        initView();
        crosswordsDaoImp=new CrosswordsDaoImp(this);
        Intent intent = getIntent();
        bookId = intent.getIntExtra(MainActivity.BOOK_ID,0);
        this.setTitle(crosswordsDaoImp.findBook(bookId).getName());
        initWordsList();
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mCheckBox = (CheckBox) findViewById(R.id.card_ck_edit);

        mCardAdapter = new CardPagerAdapter();
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        for (WordInfo wordInfo: wordsInfoList) {
            mCardAdapter.addCardItem(new CardItem(wordInfo.getWord(), wordInfo.getTranslation()));
        }
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        //没有显示的卡片数量的限制
        mViewPager.setOffscreenPageLimit(3);

        //mCheckBox.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        //界面变换视图按钮（待实现）
        // mButton.setText("card");
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        switch (v.getId()) {
            case R.id.card_btn_pdf:
//                Intent intent = new Intent(this, CreatePDFActivity.class);
//                startActivity(intent);
            default:
        }

    }

    public void initView() {
        convertToPDF = (Button) findViewById(R.id.card_btn_pdf);
        convertToPDF.setOnClickListener(this);
    }
    private void initWordsList() {
        wordsInfoList=crosswordsDaoImp.findAllWordOrderByCreateTime(bookId);
    }


    /**
     * 选择单词卡片时候的动作
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mCardShadowTransformer.enableScaling(isChecked);
        //mFragmentCardShadowTransformer.enableScaling(b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wordslist_menu, menu);
//        MenuItem item = menu.findItem(R.id.myswitch);
//        item.setIcon(R.drawable.ic_view_list_black_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.myswitch:
//                Intent intent = new Intent(CardActivity.this, WordsListActivity.class);
//                intent.putExtra(MainActivity.BOOK_ID, bookId);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);//切换界面淡入淡出效果
//                break;
            case R.id.action_edit:
                Intent intentEdit = new Intent(this, EditBookActivity.class);
                intentEdit.putExtra("bookId",bookId);
                startActivity(intentEdit);
                break;
            default:
        }
        return true;
    }
}