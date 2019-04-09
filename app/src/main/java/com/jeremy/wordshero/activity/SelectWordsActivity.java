package com.jeremy.wordshero.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.EditChecker;
import com.jeremy.wordshero.Util.TransUtil;
import com.jeremy.wordshero.bean.WordInfo;
import com.jeremy.wordshero.provider.OnTextChangeListener;
import com.jeremy.wordshero.provider.SelectWords;
import com.jeremy.wordshero.provider.SelectWordsAdapter;
import com.lvleo.dataloadinglayout.DataLoadingLayout;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;


/**
 * 扫描单词后的选词页面
 *
 * @author cyd
 * @since 2018/12/3
 */
public class SelectWordsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private FloatingActionButton addWordsButton;
    private SelectWordsAdapter adapter;
    private List<SelectWords> selectWordsList;
    private List<WordInfo> wordInfoList = new LinkedList<>();
    private static final String TAG = "SelectWordsActivity";
    private static final int UPDATE_TRANSLATION_TEXT = 1;
    public static SelectWordsActivity instance;//定义一个静态
    public CircularProgressView progressView;
    private DataLoadingLayout dataLoadingLayout;
    private int selectTranslationDialogIndex = 0;


    //语言列表
    private String[] langList = {TransUtil.LANG_NONE,
            TransUtil.LANG_ZH,
            TransUtil.LANG_DE,
            TransUtil.LANG_FRA,
            TransUtil.LANG_IT,
            TransUtil.LANG_JP,
            TransUtil.LANG_KOR,
            TransUtil.LANG_PT,
            TransUtil.LANG_RU,
            TransUtil.LANG_SPA};

    private String[] langListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_words);
        initList();
        initLangListView();

        String currentLang = Locale.getDefault().getLanguage();
        Log.d(TAG, "currentLang: "+currentLang);

        //初始化布局
        initView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SelectWordsAdapter(this, wordInfoList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemViewCacheSize(wordInfoList.size());
        //添加分割线和默认动画
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //如果每个item的高度是一定的，那么这个选项可以提高性能.
        mRecyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(new SelectWordsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //点击item右边的删除时间。
                adapter.removeItem(position);
            }
        });
        adapter.setOnTextChangeListener(new OnTextChangeListener() {
            @Override
            public void onTextChanged(int pos, String str) {
                //在这里拿到Recyclerview中的Item的position和EditText中的变化
                wordInfoList.get(pos).setWord(str);
            }

            @Override
            public void onTextChanged(int pos, String str, int flag) {

            }
        });
        instance = this;//传入当前Activity
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_word:
                //显示编辑的对话框
                addWordsDialog();
                break;
            default:
                break;
        }
    }

    public void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.select_words_recyclerView);
        addWordsButton = (FloatingActionButton) findViewById(R.id.add_word);
        addWordsButton.setOnClickListener(this);
        //点击翻译出现的进度条
        dataLoadingLayout = (DataLoadingLayout)findViewById(R.id.is_translating);

    }

    /**
     * 初始化单词列表，将识别结果的json格式字符串取回
     * 并反序列化成对象数组
     *
     * @return
     * @author cyd
     */
    public void initList() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("wordResultBundle");
        String resultJson = bundle.getString("wordResultJson");

        //这里处理json格式的字符串用的是阿里的fastJson 听说是性能最好的，我就用了
        JSONObject jsonObject = JSONObject.parseObject(resultJson);
        //取出JSON字符串中的'words_result'中的JSON对象数组。
        JSONArray jsonArray = jsonObject.getJSONArray("words_result");
        String wordsArrayJson = jsonArray.toJSONString();
        //将json数组反序列化为对象集合。
        List<SelectWords> list = JSONObject.parseArray(wordsArrayJson, SelectWords.class);

        for (SelectWords s : list) {
            //识别出的单词可能会有空格，进行一下处理
            String word = s.getWords().trim();
            //首字母大写
//            word = word.substring(0, 1).toUpperCase()+word.substring(1);


            //以"/"为分割符
            Pattern pattern = compile("[/]");
            String[] words = pattern.split(word);
            //获取分割后第一个结点，即为所需单词（删除"/"后的部分)
            word = words[0];

//            char[] cs = word.toCharArray();
//            cs[0] = Character.toLowerCase(cs[0]);
//            word = String.valueOf(cs);

            WordInfo wordInfo = new WordInfo();
            wordInfo.setWord(word);
            wordInfoList.add(wordInfo);
        }

        //若系统语言为中文，则直接显示中文翻译
        String currentLang = Locale.getDefault().getLanguage();
        Log.d(TAG, "currentLang: "+currentLang);
        if (currentLang.equals("zh")) {
            translationTest(wordInfoList, langList[1]);
        }
    }

    /**
     * 点击翻译时，显示进度条，翻译结束，隐藏进度条
     *
     * @param wordList 要翻译的单词列表
     * @param to       要翻译成什么语言
     */
    public void translationTest(final List<WordInfo> wordList, final String to) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataLoadingLayout.setVisibility(View.VISIBLE);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (WordInfo word : wordList) {
                    //把选择的每个单词翻译 语言:中文
                    String translation = TransUtil.getResultTranslation(word.getWord(), to);
                    word.setTranslation(translation);
                }

                //adapter数据更新,必须放在获得翻译结果之后
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        dataLoadingLayout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }


    /**
     * 点击添加按钮弹出的对话框
     *
     * @author cyd
     */
    private void addWordsDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View editWordsDialog = layoutInflater.inflate(R.layout.add_word_dialog_select_word, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_word);
        builder.setView(editWordsDialog);

        //获得控件
        final EditText wordNameET = (EditText) editWordsDialog.
                findViewById(R.id.add_word_text);

        //设置对话框的确定，取消事件
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得输入的字符串
                final String wordName = wordNameET.getText().toString();

                //添加空值判断
                if (!EditChecker.checkWord(wordName) || wordName.length()>=20){
                    Toast.makeText(SelectWordsActivity.this,R.string.error_word_format,Toast.LENGTH_SHORT).show();
                }else {
                    //把用户输入的数据传入对象
                    WordInfo wordInfo = new WordInfo();
                    wordInfo.setWord(wordName);
                    wordInfoList.add(0, wordInfo);
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    Toast.makeText(SelectWordsActivity.this, getString(R.string.successfully_added_word), Toast.LENGTH_SHORT).show();
                }

                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_word_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //进入选择单词本
        switch (item.getItemId()) {
            //对选择的单词进行翻译
            case R.id.translate:
                showTranslationDialog();
                break;
            case R.id.next_step:
                Intent intent = new Intent(SelectWordsActivity.this, SelectBookActivity.class);
                toLowerCase(wordInfoList);
                intent.putExtra("wordlist", (Serializable) wordInfoList);
                startActivity(intent);
                break;
            case android.R.id.home://主键id必须这样写
                onBackPressed();//按返回图标直接回退上个界面
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击翻译的翻译语言列表dialog
     */
    public void showTranslationDialog() {
        AlertDialog translationDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.translation)
                .setIcon(R.drawable.ic_g_translate_black_24dp)
                .setSingleChoiceItems(langListView, selectTranslationDialogIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //如果没有选择翻译的语种就不翻译
                        if (which == 0) {

                            for (WordInfo wordInfo : wordInfoList) {
                                wordInfo.setTranslation(null);
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            translationTest(wordInfoList, langList[which]);
                        }
                        //用变量记住上一次所选的位置
                        selectTranslationDialogIndex = which;
                        dialog.dismiss();


                    }
                }).create();
        translationDialog.show();
    }

    private void initLangListView() {
        String[] langList = {
                this.getString(R.string.no_select),
                this.getString(R.string.zh),
                this.getString(R.string.de),
                this.getString(R.string.fra),
                this.getString(R.string.it),
                this.getString(R.string.jp),
                this.getString(R.string.kor),
                this.getString(R.string.pt),
                this.getString(R.string.ru),
                this.getString(R.string.spa)
        };
        langListView = langList;
    }

    /**
     * 将单词转为小写
     *
     * @param wordInfoList
     */
    public void toLowerCase(List<WordInfo> wordInfoList) {
        for (WordInfo word : wordInfoList) {
            String oldWord = word.getWord();
            word.setWord(oldWord.toLowerCase());
        }
    }


}
