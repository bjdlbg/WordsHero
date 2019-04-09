package com.jeremy.wordshero.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.EditChecker;
import com.jeremy.wordshero.Util.SdCardUtils;
import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.GameWord;
import com.jeremy.wordshero.bean.WordInfo;
import com.jeremy.wordshero.crossword.Crossword;
import com.jeremy.wordshero.crossword.PdfGenerator;
import com.jeremy.wordshero.provider.CluesSharedPreference;
import com.jeremy.wordshero.provider.CrosswordsDaoImp;
import com.jeremy.wordshero.provider.CrosswordsGameDaoImp;
import com.jeremy.wordshero.provider.GameWordsAdapter;
import com.jeremy.wordshero.provider.ItemClickListener;
import com.jeremy.wordshero.provider.OnTextChangeListener;
import com.lvleo.dataloadinglayout.DataLoadingLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author lxh
 */
public class MakeGameActivity extends AppCompatActivity {
    public List<GameWord> gameWordList = new ArrayList<>();
    public List<GameWord> selectGameWordList = new ArrayList<>();
    public List<Game> gameListInDB = new ArrayList<>();
    public List<Integer> selectBookIdResultList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GameWordsAdapter adapter;
    private TextInputEditText titleInput;
    private TextInputEditText instructionInput;
    private TextInputEditText editText;
    private LinearLayout linearLayout;
    private ImageView randomBtn;//随机数按钮
    private TextView selectAllBtn;
    private CrosswordsDaoImp crosswordsDaoImp;
    private CrosswordsGameDaoImp crosswordsGameDaoImp;
    private PdfGenerator mPdfGenerator;
    private DataLoadingLayout dataLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_game);
        //初始化布局和控件
        initView();
        //创建文件夹
        initDir();
        //初始化单词列表
        crosswordsDaoImp = new CrosswordsDaoImp(this);
        crosswordsGameDaoImp = new CrosswordsGameDaoImp(this);
        initGameWordsList();
        adapter = new GameWordsAdapter(this,gameWordList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //解决RecyclerView 滑动卡顿
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        selectWordEvent();

    }

    private void selectWordEvent() {
        //开始时默认全选
        for (GameWord gameWord : gameWordList) {
            gameWord.setChecked(true);
            selectGameWordList.add(gameWord);

        }
        editText.setText(selectGameWordList.size() + "");
        //全选的点击事件
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectAllBtn.getText().toString().equals(getString(R.string.select_all))) {
                    selectAllBtn.setText(getString(R.string.deselect_all));
                    selectGameWordList.clear();
                    for (GameWord gameWord : gameWordList) {
                        gameWord.setChecked(true);
                        selectGameWordList.add(gameWord);
                        editText.setText(selectGameWordList.size() + "");
                    }
                } else {
                    selectAllBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                    selectAllBtn.setText(getString(R.string.select_all));
                    selectGameWordList.clear();
                    for (GameWord gameWord : gameWordList) {
                        gameWord.setChecked(false);
                    }
                    editText.setText(selectGameWordList.size() + "");
                }
                adapter.notifyDataSetChanged();
            }
        });

        //随机按钮点击事件
        randomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mNumber = editText.getText().toString();
                //判断随机数的大小，只能小于用户单词总个数
                if (mNumber.equals("") ||
                        Integer.parseInt(mNumber) <= 0 ||
                        Integer.parseInt(mNumber) >= gameWordList.size()) {
                    //灰色不可点骰子
                    randomBtn.setImageResource(R.drawable.touzifalse);
                    randomBtn.setFocusable(false);
                    Toast.makeText(getApplicationContext(), R.string.random_word_count_limit, Toast.LENGTH_SHORT).show();
                } else {
                    //可点骰子
                    randomBtn.setImageResource(R.drawable.touzitrue);
                    //随机选取item
                    randomChecked();
                }

            }
        });
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSlideLeft(int position, View view) {

            }

            @Override
            public void onItemClicked(int position, View v) {
                if (gameWordList.get(position).isChecked() || selectGameWordList.size() >= 15) {
                    selectGameWordList.remove(gameWordList.get(position));
                    gameWordList.get(position).setChecked(false);

                    editText.setText(selectGameWordList.size() + "");
                } else {
                    selectGameWordList.add(gameWordList.get(position));
                    gameWordList.get(position).setChecked(true);
                    editText.setText(selectGameWordList.size() + "");
                }
                if (selectGameWordList.size() != gameWordList.size()) {
                    selectAllBtn.setText(getString(R.string.select_all));
                } else {
                    selectAllBtn.setText(getString(R.string.deselect_all));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClicked(int position, View v) {
                return false;
            }
        });
        adapter.setOnTextChangeListener(new OnTextChangeListener() {
            @Override
            public void onTextChanged(int pos, String str) {
                gameWordList.get(pos).setClue(str);
                if (gameWordList.get(pos).isChecked()) {

                    for (GameWord gameWord : selectGameWordList) {
                        if (gameWord.equals(gameWordList.get(pos).getWord())) {
                            gameWord.setClue(str);
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(int pos, String str, int flag) {

            }
        });
    }


    private void randomChecked() {
        int amount = Integer.parseInt(editText.getText().toString());
        int n = gameWordList.size();
        if (amount > n) {
            amount = n;
        }
        int[] randArr = new int[amount];
        int i = 0;
        while (i < amount) {
            int rand = (new Random().nextInt(n));
            boolean isRandExist = false;
            for (int j = 0; j < randArr.length; j++) {
                if (randArr[j] == rand) {
                    isRandExist = true;
                    break;
                }
            }
            if (!isRandExist) {
                randArr[i] = rand;
                i++;
            }
        }
        selectGameWordList.clear();
        for (GameWord gameWord : gameWordList) {
            gameWord.setChecked(false);
        }
        for (int m = 0; m < randArr.length; m++) {
            gameWordList.get(randArr[m]).setChecked(true);
            selectGameWordList.add(gameWordList.get(randArr[m]));
            editText.setText(selectGameWordList.size() + "");
        }
        adapter.notifyDataSetChanged();

    }

    private void initGameWordsList() {
        Intent intent = getIntent();
        selectBookIdResultList = intent.getIntegerArrayListExtra("booklist");
        //当选择单词本中单词个数超过15个时跳出（crossword算法有限制）
        lable:
        for (int selectBookId : selectBookIdResultList) {
            for (WordInfo wordInfo : crosswordsDaoImp.findAllWord(selectBookId)) {
                GameWord gameWord = new GameWord();
                gameWord.setWord(wordInfo.getWord());
                //线索默认是翻译
                gameWord.setClue(CluesSharedPreference.getCluesByWord(this,wordInfo.getWord()));
                gameWord.setBook(crosswordsDaoImp.findBook(wordInfo.getBook_id()).getName());
                gameWordList.add(gameWord);
                if (gameWordList.size() >= 15) {
                    Toast.makeText(getApplicationContext(), "The maximum number of words is 15", Toast.LENGTH_SHORT).show();
                    break lable;
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.generate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //实体化创建PDF生成器
        mPdfGenerator = new PdfGenerator();

        switch (item.getItemId()) {
            case R.id.action_complete:
                createCrossWdPdf(mPdfGenerator);
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    public void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.make_game_word_recycler_view);
        editText = (TextInputEditText) findViewById(R.id.amount_edit_text);
        linearLayout = (LinearLayout) findViewById(R.id.random_edit_layout);
        randomBtn = (ImageView) findViewById(R.id.random_btn);
        selectAllBtn = (TextView) findViewById(R.id.select_all_btn);
        titleInput = (TextInputEditText) findViewById(R.id.title_create);
        instructionInput = (TextInputEditText) findViewById(R.id.instruction_create);
        dataLoadingLayout = (DataLoadingLayout) findViewById(R.id.creating_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    //在sd卡下创建文件夹
    public void initDir() {
        if (SdCardUtils.checkSdCard()) {
            SdCardUtils.createFileDir(SdCardUtils.FILE_DIR);
            SdCardUtils.createFileDir(SdCardUtils.FILE_DIR + SdCardUtils.PDF_DIR);
        }
    }


    public void createCrossWdPdf(final PdfGenerator pdfGenerator) {
        //判断标题是否与数据库已有的游戏重复
        boolean titleIsRepeat = false;
        final String title = titleInput.getText().toString();
        gameListInDB = crosswordsGameDaoImp.findAllGame();
        for (Game game : gameListInDB) {
            if (game.getTitle().equals(title)) {
                titleIsRepeat = true;
                break;
            }
        }
        if (EditChecker.isEmpty(title)) {//标题是否为空
            Toast.makeText(this, getString(R.string.empty_title), Toast.LENGTH_SHORT).show();
        } else if (titleIsRepeat) {//标题是否重复
            Toast.makeText(this, getString(R.string.title_repeated), Toast.LENGTH_SHORT).show();
        } else if (selectGameWordList.size() == 0) {//是否选择了单词
            Toast.makeText(this, getString(R.string.empty_word_list), Toast.LENGTH_SHORT).show();
        } else if (hasCluesEmpty(selectGameWordList)) {//是否缺少线索

        } else {
            dataLoadingLayout.setVisibility(View.VISIBLE);
            final String instruction = instructionInput.getText().toString();
            //申请写入文件权限
            if (ContextCompat.checkSelfPermission(MakeGameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MakeGameActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                pdfGenerator.createPdf(replaceSpace(selectGameWordList),
                                        title, instruction, Crossword.CREATEGAME);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MakeGameActivity.this, R.string.successful_create_pdf, Toast.LENGTH_SHORT).show();
                                    dataLoadingLayout.setVisibility(View.GONE);
                                    final File file = new File(Environment.getExternalStorageDirectory() + SdCardUtils.FILE_DIR + SdCardUtils.PDF_DIR,
                                            title + ".PDF");

                                    Snackbar.make(findViewById(R.id.title_create),
                                            getString(R.string.save_path) + file.getPath(), Snackbar.LENGTH_LONG)
                                            .setAction(getString(R.string.open_pdf), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent("android.intent.action.VIEW");
                                                    intent.addCategory("android.intent.category.DEFAULT");
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                                                            "com.luckyxmobile.CrossWords.fileprovider", file);
                                                    intent.setDataAndType(uri, "application/pdf");
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .show();

                                }

                            });
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MakeGameActivity.this, getString(R.string.fail_create_pdf), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public List<GameWord> replaceSpace(List<GameWord> selectGameWordList) {
        List<GameWord> list = selectGameWordList;
        for (GameWord word : list) {
            word.setWord(word.getWord().replaceAll(" ", ""));
        }
        return list;
    }

    //判断是否有单词的线索为空 有的话返回true  没有返回false
    public boolean hasCluesEmpty(List<GameWord> selectGameWordList) {
        List<GameWord> list = selectGameWordList;
        for (GameWord word : list) {
            if (EditChecker.isEmpty(word.getClue())) {
                Toast.makeText(MakeGameActivity.this,
                        word.getWord() + getString(R.string.missing_clue), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;

    }


}
