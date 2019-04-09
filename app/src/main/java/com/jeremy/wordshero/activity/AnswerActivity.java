package com.jeremy.wordshero.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.GameWord;
import com.jeremy.wordshero.provider.CrosswordsGameDaoImp;

import java.util.ArrayList;
import java.util.List;

/**
 * 该activity显示对应游戏的答案
 *
 * @author jixiang
 * @date 2019-1-18
 */
public class AnswerActivity extends AppCompatActivity {
    private TextView mTitle, mTextView, mAnswer;
    private Handler mHandler = new Handler();
    private CrosswordsGameDaoImp crosswordsGameDaoImp;
    private List<GameWord> gameWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        setTitle("Answer");
        Intent intent = getIntent();
        //传递游戏标题
        mTitle = findViewById(R.id.game_title);
        mTitle.setText(intent.getStringExtra("title"));
        mTextView = findViewById(R.id.game_instruction);
        mAnswer = findViewById(R.id.answer_text);
        crosswordsGameDaoImp = new CrosswordsGameDaoImp(this);
        int gameId = intent.getIntExtra("gameId", 0);
        showAnswer(gameId);
        mHandler.postDelayed(runnable, 1000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //简单淡出效果
            try {
                mTextView.setVisibility(View.VISIBLE);
                mAnswer.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    /**
     * 查看答案（显示对应的单词标号和线索）
     */
    public void showAnswer(int gameId) {
        gameWords = crosswordsGameDaoImp.findAllGameWordByGameId(gameId);
        String answer = "";
        for (int i = 0; i < gameWords.size(); i++) {
            answer += (i + 1) + ":" + gameWords.get(i).getWord() + "\n";
        }
        mAnswer.setText(answer);
    }
}
