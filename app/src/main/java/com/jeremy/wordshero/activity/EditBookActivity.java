package com.jeremy.wordshero.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.Book;
import com.jeremy.wordshero.bean.WordInfo;
import com.jeremy.wordshero.provider.CrosswordsDaoImp;
import com.jeremy.wordshero.provider.EditBookWordsAdapter;
import com.jeremy.wordshero.provider.OnTextChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 从单词本中编辑单词列表
 * @author
 */
public class EditBookActivity extends AppCompatActivity {
    private EditText nEditText;
    private EditText iEditText;
    private String mBookName;
    private String mBookInroduction;
    private RecyclerView recyclerView;
    private EditBookWordsAdapter adapter;
    private ActionBar supportActionBar;
    public List<WordInfo> wordsInfoList = new ArrayList<>();
    private static final String TAG = "EditBookActivity";
    private int bookId;
    private Book book;
    private CrosswordsDaoImp crosswordsDaoImp;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        crosswordsDaoImp = new CrosswordsDaoImp(this);
        //初始化布局和数据
        initView();
        initData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                //禁止垂直滑动
                return false;
            }

            @Override
            public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EditBookWordsAdapter(this, wordsInfoList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //解决RecyclerView 滑动卡顿
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,adapter.getItemCount()-1);

        mBookName = nEditText.getText().toString();
        mBookInroduction = nEditText.getText().toString();

        Book book = (Book) getIntent().getSerializableExtra("book");//获取对象
        if (book != null) {
            nEditText.setText(book.getName());
            iEditText.setText(book.getIntroduction());
        }
        adapter.setOnTextChangeListener(new OnTextChangeListener() {
            @Override
            public void onTextChanged(int pos, String str) {
            }

            @Override
            public void onTextChanged(int pos, String str, int flag) {
                if (flag == 0) {
                    wordsInfoList.get(pos).setWord(str);
                } else if (flag == 1) {
                    wordsInfoList.get(pos).setTranslation(str);
                }
            }
        });

    }


    //初始化布局
    public void initView() {
        nEditText = (EditText) findViewById(R.id.book_name_edit);
        iEditText = (EditText) findViewById(R.id.book_introduction_edit);
        recyclerView = (RecyclerView) findViewById(R.id.edit_book_recyclerView);
        supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setTitle(R.string.edit_word_book);
        Intent intent = getIntent();
        book = new Book();
        bookId = intent.getIntExtra("bookId", 0);
        book = crosswordsDaoImp.findBook(bookId);
        nEditText.setText(book.getName());
        iEditText.setText(book.getIntroduction());
    }

    public void initData() {
        initWordsList();

    }

    private void initWordsList() {
        wordsInfoList = crosswordsDaoImp.findAllWord(bookId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_complete:
                book.setName(nEditText.getText().toString());
                book.setIntroduction(iEditText.getText().toString());
                book.setUpdateTime(System.currentTimeMillis());
                crosswordsDaoImp.updateBook(book);
                for (WordInfo wordInfo : wordsInfoList) {
                    wordInfo.setUpdate_time(System.currentTimeMillis());
                    crosswordsDaoImp.updateWord(wordInfo);
                }
                finish();
                break;
            case android.R.id.home://主键id必须这样写
                onBackPressed();//按返回图标直接回退上个界面
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
