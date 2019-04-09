package com.jeremy.wordshero.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.EditChecker;
import com.jeremy.wordshero.bean.WordInfo;
import com.jeremy.wordshero.provider.CluesSharedPreference;
import com.jeremy.wordshero.provider.CommonAdapter;
import com.jeremy.wordshero.provider.CrosswordsDaoImp;
import com.jeremy.wordshero.provider.ViewHolder;
import com.jeremy.wordshero.view.SwipeMenuLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxh
 */
public class WordsListActivity extends AppCompatActivity {
    public List<WordInfo> wordsInfoList = new ArrayList<>();
    private int bookId;
    FloatingActionButton addFab;
    private CrosswordsDaoImp crosswordsDaoImp;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);
        crosswordsDaoImp = new CrosswordsDaoImp(this);
        Intent intent = getIntent();
        bookId = intent.getIntExtra(MainActivity.BOOK_ID, 0);
        this.setTitle(crosswordsDaoImp.findBook(bookId).getName());

    }

    @Override
    public void onResume() {
        super.onResume();
        initWordsList();
        //把单词和翻译存到sp里备份.等到创建游戏时添加默认线索时用,默认线索就是翻译
        for (WordInfo wordInfo : wordsInfoList) {
            CluesSharedPreference.setCluesByWord(this,wordInfo.getWord(),wordInfo.getTranslation());
        }
        initView();
        listView.setAdapter(new CommonAdapter<WordInfo>(this, wordsInfoList, R.layout.word_item) {

            @Override
            public void convert(final ViewHolder holder, WordInfo testBean, final int position, final View convertView) {
                holder.setText(R.id.english_vocabulary, testBean.getWord());
                holder.setText(R.id.word_translation, testBean.getTranslation());
//                if (testBean.isHaveShowTranslation()) {
//                    holder.setVisible(R.id.word_translation, true);
//                } else {
//                    holder.setVisible(R.id.word_translation, false);
//                }
                //可以根据自己需求设置一些选项(这里设置了IOS阻塞效果以及item的依次左滑、右滑菜单)
                ((SwipeMenuLayout) holder.getConvertView()).setIos(true).setLeftSwipe(true);
                //监听事件
//                holder.setOnClickListener(R.id.ll_content, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //二次点击，隐藏翻译
//                        if (wordsInfoList.get(position).isHaveShowTranslation()) {
//                            holder.setVisible(R.id.word_translation, false);
//                            wordsInfoList.get(position).setHaveShowTranslation(false);
//                        } else {
//                            holder.setVisible(R.id.word_translation, true);
//                            if (holder.getText(R.id.word_translation) == "") {
//                                Toast.makeText(WordsListActivity.this, "You didn't save the translation for this word", Toast.LENGTH_SHORT).show();
//                            }
//                            wordsInfoList.get(position).setHaveShowTranslation(true);
//                        }
//
//                    }
//                });
                holder.setOnClickListener(R.id.delete_icon, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        //在ListView里，点击侧滑菜单上的选项时，如果想让侧滑菜单同时关闭，调用这句话
                        ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                        //删除操作
                        final WordInfo deleteWord = wordsInfoList.get(position);
                        deleteWord.setHaveShowTranslation(false);//隐藏翻译，免得删除后item显示混乱
                        wordsInfoList.remove(position);
                        crosswordsDaoImp.removeWord(deleteWord.getId());
                        notifyDataSetChanged();
                        //用于向用户提示消息，并显示撤销删除操作的提示

                        Snackbar.make(v, getString(R.string.delete_word), Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.undo), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //若点击UNDO，则撤销删除
                                        wordsInfoList.add(position, deleteWord);
                                        notifyDataSetChanged();
                                        crosswordsDaoImp.addWord(deleteWord);

                                    }
                                })
                                .setActionTextColor(Color.parseColor("#F9AA33"))
                                .show();


                    }
                });
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWordsDialog();
            }
        });

    }


    /**
     * 初始化控件
     */
    public void initView() {
        listView = (ListView) findViewById(R.id.listView);
        addFab = (FloatingActionButton) findViewById(R.id.add_fab);
    }

    private void initWordsList() {
        wordsInfoList = crosswordsDaoImp.findAllWordOrderByCreateTime(bookId);
    }


    private void addWordsDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View editWordsDialog = layoutInflater.inflate(R.layout.add_words_dialog_in_word_book, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_word);
        builder.setView(editWordsDialog);

        //获得控件
        final EditText wordNameET = (EditText) editWordsDialog.
                findViewById(R.id.add_word_text);
        wordNameET.setHint(R.string.word);
        final EditText wordTranslationET = (EditText) editWordsDialog.
                findViewById(R.id.add_word_translation_text);
        wordTranslationET.setHint(R.string.translation);
        //设置对话框的确定，取消事件
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            /**
             * ok按钮点击事件
             * 获取并验证文本，数据保存，刷新列表
             * */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得输入的字符串
                final String wordName = wordNameET.getText().toString();
                final String wordTranslation = wordTranslationET.getText().toString();
                //添加对文本框单词输入格式的验证，只允许英文字母（对线索格式不限制）
                if (!EditChecker.checkWord(wordName) || wordName.length() >= 20) {
                    //格式错误
                    Toast.makeText(WordsListActivity.this, R.string.error_word_format,
                            Toast.LENGTH_SHORT).show();
                } else {
                    //把用户输入的数据传入对象
                    WordInfo selectWords = new WordInfo(wordName, wordTranslation);
                    selectWords.setCrate_time(System.currentTimeMillis());
                    selectWords.setBook_id(bookId);
                    wordsInfoList.add(0, selectWords);
                    //存入数据库
                    if (crosswordsDaoImp.addWord(selectWords) > 0) {
                        Toast.makeText(WordsListActivity.this, getString(R.string.successfully_added_word),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(WordsListActivity.this, "单词添加失败",
                                Toast.LENGTH_SHORT).show();
                    }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wordslist_menu, menu);
//        MenuItem item = menu.findItem(R.id.myswitch);
//        item.setIcon(R.drawable.ic_view_carousel_black_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.myswitch:
//                Intent intent = new Intent(WordsListActivity.this, CardActivity.class);
//                intent.putExtra(MainActivity.BOOK_ID, bookId);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);//切换界面淡入淡出效果
//                break;
            case R.id.action_edit:
                Intent intentEdit = new Intent(this, EditBookActivity.class);
                intentEdit.putExtra("bookId", bookId);
                startActivity(intentEdit);
                break;
            default:
        }
        return true;
    }


}
