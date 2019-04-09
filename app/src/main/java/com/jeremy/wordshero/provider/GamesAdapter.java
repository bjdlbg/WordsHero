package com.jeremy.wordshero.provider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.Games;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.graphics.Typeface.createFromAsset;

/**
 * 该列表显示生成的游戏
 *
 * @author lxh
 */
public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.ViewHolder> {
    //游戏上item，编辑按钮和查看Answer的点击事件接口
    private Games.ItemEditBtnClickListener mItemEditBtnClickListener;
    private Games.ItemAnswerTvClickListener mItemAnswerTvClickListener;
    private Games.ItemShowPdfListener mItemShowPdfListener;
    private Games.ItemDeleteGameListener mItemDeleteGameLister;
    private float mDensity;
    private int mHiddenViewMeasuredHeight;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.games_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Game game = mGamesList.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gameInstruction.setText(game.getInstruction());
        Date date = new Date();
        date.setTime(game.getCreateTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.gameCreateTime.setText(simpleDateFormat.format(date));
        //设置字体
        holder.gameCount.setText(game.getCount() + "");
        Typeface typeface = createFromAsset(holder.itemView.getResources().getAssets(), "fonts/orange juice 2.0.ttf");
        holder.gameCount.setTypeface(typeface);
        if (game.isCheckBoxIsShow()) {
            holder.gameCheckBox.setVisibility(View.VISIBLE);
        }else{
            holder.gameCheckBox.setVisibility(View.GONE);
        }
        if(game.isCheckBoxIsSelected()){
            holder.gameCheckBox.setChecked(true);
        }else{
            holder.gameCheckBox.setChecked(false);
        }
        //获取像素密度
        mDensity = holder.itemView.getResources().getDisplayMetrics().density;
        //获取布局的高度
        mHiddenViewMeasuredHeight = (int) (mDensity * 40 + 0.5);
        //编辑按钮事件
       /* holder.editGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemEditBtnClickListener.onItemEditBtnClick(position);
            }
        });*/
        //查看Answer
       /* holder.gameAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemAnswerTvClickListener.
                        onItemAnswerTvClick(position, holder.gameTitle, holder.gameTitle.getText().toString());

            }
        });*/
        //item点击之后预览pdf
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemShowPdfListener.onItemClick(position);
            }
        });
        //长按打开两个按钮
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               /* //打开动画
                animateOpen(holder.linearLayout);

              final Handler handler=new Handler();
              handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      animateClose(holder.linearLayout);
                  }
              },1500);*/
                mItemDeleteGameLister.onItemLongClick(position);
                return true;
            }
        });

    }

    private void animateOpen(final View view) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(view, 0, mHiddenViewMeasuredHeight);
        animator.start();
    }

    private void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    @Override
    public int getItemCount() {
        return mGamesList.size();
    }
    public void deleteData(int pos){
    }

    private List<Game> mGamesList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitle;
        TextView gameInstruction;
        //编辑游戏按钮
        Button editGameBtn;
        //查看答案的按钮
        Button gameAnswer;
        TextView gameCreateTime;
        LinearLayout linearLayout;
        TextView gameCount;
        CheckBox gameCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            gameTitle = (TextView) itemView.findViewById(R.id.game_title);
            gameInstruction = (TextView) itemView.findViewById(R.id.game_instruction);
            //editGameBtn = (Button) itemView.findViewById(R.id.edit_game_btn);
            //gameAnswer =  itemView.findViewById(R.id.show_answer_btn);
            gameCreateTime = (TextView) itemView.findViewById(R.id.game_create_time);
            //linearLayout = itemView.findViewById(R.id.hidden_view);
            gameCount = itemView.findViewById(R.id.text_count);
            gameCheckBox = itemView.findViewById(R.id.game_check);
        }
    }

    public GamesAdapter(List<Game> gamesList) {
        mGamesList = gamesList;
    }

    public void setmGamesList(List<Game> mGamesList) {
        this.mGamesList = mGamesList;
    }

    /**
     * 提供item和两个按钮的对外调用
     */
    public void setItemEditBtnClickListener(Games.ItemEditBtnClickListener itemEditBtnClickListener) {
        mItemEditBtnClickListener = itemEditBtnClickListener;
    }

    public void setItemAnswerTvClickListener(Games.ItemAnswerTvClickListener itemAnswerTvClickListener) {
        mItemAnswerTvClickListener = itemAnswerTvClickListener;
    }

    public void setItemClickListener(Games.ItemShowPdfListener itemClickListener) {
        mItemShowPdfListener = itemClickListener;
    }

    public void setItemDeleteGameLister(Games.ItemDeleteGameListener itemDeleteGameLister) {
        mItemDeleteGameLister = itemDeleteGameLister;
    }

}
