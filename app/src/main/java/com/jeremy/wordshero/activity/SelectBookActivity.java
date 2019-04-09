package com.jeremy.wordshero.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.Book;
import com.jeremy.wordshero.bean.WordInfo;
import com.jeremy.wordshero.provider.BooksAdapter;
import com.jeremy.wordshero.provider.CrosswordsDaoImp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxh
 */
public class SelectBookActivity extends AppCompatActivity {
    private List<Book> bookNameList = new ArrayList<>();
    private List<WordInfo> wordInfoList = new ArrayList<>();
    public ArrayList<Integer> selectBookList = new ArrayList<>();
    private String bookName;
    private BooksAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout noBookActivityLayout;
    private String wordsGameFragmentClass;
    private int selectBookId;
    private CrosswordsDaoImp crosswordsDaoImp;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_book);
        crosswordsDaoImp = new CrosswordsDaoImp(this);
        Intent intent = getIntent();
        wordsGameFragmentClass = intent.getStringExtra("class");
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setTitle(R.string.select_word_book);
        recyclerView = (RecyclerView) findViewById(R.id.add_book_recycler_view);
        noBookActivityLayout=(LinearLayout)findViewById(R.id.nobook_activity_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initBookInfo();
        adapter = new BooksAdapter(bookNameList, "SelectBookActivity");
        recyclerView.setAdapter(adapter);
        onClickListener();
        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.select_book_add_fab);
        if (wordsGameFragmentClass != null) {
            addFab.setVisibility(View.GONE);
        }
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookDialog();
            }
        });

    }

    private void addBookDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View editWordsDialog = layoutInflater.inflate(R.layout.add_book_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_word_book);
        builder.setView(editWordsDialog);

        //获得控件
        final EditText bookNameET = (EditText) editWordsDialog.
                findViewById(R.id.add_book_text);
        bookNameET.setHint(R.string.book_title);
        final EditText bookInstructionET = (EditText) editWordsDialog.
                findViewById(R.id.add_instruction_text);
        bookInstructionET.setHint(R.string.book_instruction);
        //设置对话框的确定，取消事件
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得输入的字符串
                final String bookName = bookNameET.getText().toString();
                final String bookInstruction = bookInstructionET.getText().toString();
                //输入的名称不能为空
                if (!bookName.equals("")) {
                    //把用户输入的数据传入对象
                    Book book = new Book(bookName, bookInstruction);
                    book.setCreateTime(System.currentTimeMillis());
                    //存入数据库
                    if (crosswordsDaoImp.addBook(book) > 0) {
                        Toast.makeText(SelectBookActivity.this, R.string.add_word_book_success,
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(SelectBookActivity.this, R.string.add_word_book_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),R.string.empty_title,Toast.LENGTH_SHORT)
                            .show();
                }
                onResume();

                try {
                    Field field = dialog.getClass().
                            getSuperclass().
                            getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().
                            getSuperclass().
                            getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //设置帮助布局 的显隐
        if (initBookInfo()){
            recyclerView.setVisibility(View.VISIBLE);
            noBookActivityLayout.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            noBookActivityLayout.setVisibility(View.VISIBLE);
        }
        adapter = new BooksAdapter(bookNameList, "SelectBookActivity");
        recyclerView.setAdapter(adapter);
        onClickListener();
    }

    private void onClickListener() {
        adapter.setItemClickListener(new BooksAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (wordsGameFragmentClass == null) {
                    bookName = bookNameList.get(position).getName();
                    selectBookId = bookNameList.get(position).getId();
                    for (Book book : bookNameList) {
                        book.setChecked(false);
                    }
                    bookNameList.get(position).setChecked(true);
                    adapter.notifyDataSetChanged();
                } else {
                    if (bookNameList.get(position).isChecked()) {
                        selectBookList.remove((Integer) bookNameList.get(position).getId());
                        adapter.notifyDataSetChanged();
                        bookNameList.get(position).setChecked(false);
                    } else {
                        selectBookList.add(bookNameList.get(position).getId());
                        bookNameList.get(position).setChecked(true);
                    }
                }

                adapter.notifyDataSetChanged();
            }


        });
    }

    private boolean initBookInfo() {
        bookNameList = crosswordsDaoImp.findAllBookOrderByCreateTime();
        if (bookNameList.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        if (wordsGameFragmentClass != null) {
            MenuItem item = menu.findItem(R.id.action_complete);
            item.setIcon(R.drawable.ic_navigate_next_black_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_complete:
                if (wordsGameFragmentClass == null) { //来自选词页面
                   if (selectBookId != 0) {
                       saveWord();
                       Intent intent = new Intent(this, WordsListActivity.class);
                       intent.putExtra(MainActivity.BOOK_ID, selectBookId);
                       Log.d("selectBookActivity", "selectBookId:"+selectBookId);
                       startActivity(intent);
                       if (SelectWordsActivity.instance != null) { //来自创建游戏的主页面
                           SelectWordsActivity.instance.finish(); //销毁指定Activity
                       }
                       finish();
                   } else {
                       Toast.makeText(SelectBookActivity.this,
                               getString(R.string.empty_select_book),
                               Toast.LENGTH_LONG).show();
                   }

                } else {
                    //判断用户是否选择了单词本
                    if (selectBookList.size() == 0) {
                        Toast.makeText(SelectBookActivity.this,
                                getString(R.string.empty_select_book),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("selectBookActivity", selectBookList.size()+"");
                        Intent intent = new Intent(this, MakeGameActivity.class);
                        intent.putIntegerArrayListExtra("booklist", selectBookList);
                        startActivity(intent);
                        finish();
                    }
                }

                break;
            case android.R.id.home://主键id必须这样写
                onBackPressed();//按返回图标直接回退上个界面
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveWord() {
        wordInfoList = (ArrayList<WordInfo>) getIntent().getSerializableExtra("wordlist");
        int insertWordNumber = 0;
        boolean isSuccess = true;
        if (wordInfoList != null) {
            for (WordInfo wordInfo : wordInfoList) {
                wordInfo.setWord(wordInfo.getWord().replace(" ", ""));
                wordInfo.setBook_id(selectBookId);
                wordInfo.setCrate_time(System.currentTimeMillis());
                if (crosswordsDaoImp.addWord(wordInfo) > 0) {
                    insertWordNumber++;
                } else {
                    isSuccess = false;
                }
            }
            if (isSuccess) {
                Toast.makeText(SelectBookActivity.this, R.string.successfully_added_word,
                        Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(SelectBookActivity.this, R.string.fail_added_word,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
