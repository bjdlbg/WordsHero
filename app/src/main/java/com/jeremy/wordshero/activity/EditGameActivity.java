package com.jeremy.wordshero.activity;

import android.Manifest;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;
import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.LogUtil;
import com.jeremy.wordshero.Util.SdCardUtils;
import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.GameWord;
import com.jeremy.wordshero.crossword.PdfGenerator;
import com.jeremy.wordshero.provider.CrosswordsGameDaoImp;
import com.jeremy.wordshero.provider.GameWordsAdapter;
import com.jeremy.wordshero.provider.OnTextChangeListener;

import com.lvleo.dataloadinglayout.DataLoadingLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxh
 * @date 2019/1/21
 */
public class EditGameActivity extends AppCompatActivity {
    //游戏单词列表
    public List<GameWord> gameWordList = new ArrayList<>();
    //游戏列表
    public List<Game> gameListInDB = new ArrayList<>();
    private RecyclerView recyclerView;
    private GameWordsAdapter adapter;
    private TextInputEditText titleInput;
    private TextInputEditText instructionInput;
    private CrosswordsGameDaoImp crosswordsGameDaoImp;
    private Game game;
    private int gameId;
    //生成过程中的加载动画
    private DataLoadingLayout dataLoadingLayout;
    private PdfGenerator mPdfGenerator;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);
        crosswordsGameDaoImp = new CrosswordsGameDaoImp(this);
        initData();
        initView();
        setWordEvent(adapter);
    }

    private void initView() {
        recyclerView = findViewById(R.id.edit_game_word_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        titleInput = findViewById(R.id.title_edit);
        instructionInput = findViewById(R.id.instruction_edit);
        titleInput.setText(game.getTitle());
        instructionInput.setText(game.getInstruction());
        adapter = new GameWordsAdapter(gameWordList, 1);
        dataLoadingLayout = (DataLoadingLayout) findViewById(R.id.save_edit_layout);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void initData() {
        Intent intent = getIntent();
        gameId = intent.getIntExtra("gameId", 0);
        gameWordList = crosswordsGameDaoImp.findAllGameWordByGameId(gameId);
        game = crosswordsGameDaoImp.findGameById(gameId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //实体化创建PDF生成器
        mPdfGenerator = new PdfGenerator();
        switch (item.getItemId()) {
            case R.id.action_delete:
                //当删除时显示提示框
                showDeleteDialog();
                break;
            case R.id.action_edit_save:
                //判断标题是否与数据库已有的游戏重复
                boolean titleIsRepeat = false;
                final String title = titleInput.getText().toString();
                gameListInDB = crosswordsGameDaoImp.findAllGame();
                for (Game game : gameListInDB) {
                    if (game.getTitle().equals(title) && game.getId() != gameId) {
                        titleIsRepeat = true;
                        break;
                    }
                }
                //若标题重复则提示用户
                if (titleIsRepeat) {
                    Toast.makeText(this, R.string.title_repeated, Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    //开始显示生成动画
                    dataLoadingLayout.setVisibility(View.VISIBLE);
                    final String instruction = instructionInput.getText().toString();
                    //申请写入文件权限
                    if (ContextCompat.checkSelfPermission(EditGameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditGameActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //打印PDF
                                    try {
                                        mPdfGenerator.createPdf(gameWordList, title, instruction,gameId);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (DocumentException e) {
                                        e.printStackTrace();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(EditGameActivity.this, R.string.successful_create_pdf, Toast.LENGTH_SHORT).show();
                                            dataLoadingLayout.setVisibility(View.GONE);
                                            final File file = new File(Environment.getExternalStorageDirectory() + SdCardUtils.FILE_DIR + SdCardUtils.PDF_DIR,
                                                    title + ".PDF");
                                            Snackbar.make(findViewById(R.id.instruction_edit),
                                                    getString(R.string.save_path) + file.getPath(), Snackbar.LENGTH_LONG)
                                                    .setAction(getString(R.string.open_pdf),new View.OnClickListener() {
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
                            Toast.makeText(EditGameActivity.this, R.string.fail_create_pdf, Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    public void setWordEvent(GameWordsAdapter wordsAdapter) {
        wordsAdapter.setOnTextChangeListener(new OnTextChangeListener() {
            @Override
            public void onTextChanged(int pos, String str) {
                gameWordList.get(pos).setClue(str);
            }

            @Override
            public void onTextChanged(int pos, String str, int flag) {

            }
        });
    }



    //当删除时显示提示框
    private void showDeleteDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_to_delete_game)
                .setTitle(R.string.delete_game)
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton(R.string.delete_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pdfPath = crosswordsGameDaoImp.findGameById(gameId).getPdfPath();
                        LogUtil.d("filePath", "  pdfPath:" + pdfPath);
                        File pdfFile = new File(pdfPath);
                        if (pdfFile.exists()) {
                            pdfFile.delete();
                        }
                        //即使文件不存在，数据库也要将游戏删除

                        crosswordsGameDaoImp.deleteGameWordByGameId(gameId);
                        crosswordsGameDaoImp.deleteGameById(gameId);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialog.show();
    }
}
