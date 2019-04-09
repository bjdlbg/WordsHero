package com.jeremy.wordshero.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.GameWord;


import java.util.List;

/**
 * @author lxh
 */
public class GameWordsAdapter extends RecyclerView.Adapter<GameWordsAdapter.ViewHolder> {
    private List<GameWord> gameWordList;
    private ItemClickListener itemClickListener;
    private OnTextChangeListener mTextListener;
    private int flag;
    private Context mContext;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.itemClickListener = mItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText gwEditText;//word
        //TextInputEditText wcEditText;//clues
        AutoCompleteTextView wcEditText;
        TextView wbTextView;//book 笔记本名
        CheckBox wCheckBox;
        ImageView mImageViewBook;//笔记本图标

        public ViewHolder(View itemView) {
            super(itemView);
            gwEditText = (TextInputEditText) itemView.findViewById(R.id.game_words_edit_view);
            //wcEditText = (TextInputEditText) itemView.findViewById(R.id.word_clue_edit_view);
            wcEditText = (AutoCompleteTextView) itemView.findViewById(R.id.word_clue_edit_view);
            wbTextView = (TextView) itemView.findViewById(R.id.word_book_from);
            wCheckBox = (CheckBox) itemView.findViewById(R.id.word_check);
            mImageViewBook = (ImageView)itemView.findViewById(R.id.item_book_image);
        }
    }

    public GameWordsAdapter(Context context ,List<GameWord> gameWordList) {
        this.mContext =context;
        this.gameWordList = gameWordList;
    }

    public GameWordsAdapter(List<GameWord> gameWordList, int flag) {
        this.gameWordList = gameWordList;
        this.flag = flag;
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.mTextListener = onTextChangeListener;
    }

    @NonNull
    @Override
    public GameWordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.game_words_item, parent, false));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (itemClickListener != null) {
                    itemClickListener.onItemClicked(position, v);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GameWordsAdapter.ViewHolder holder, final int position) {
        final GameWord gameWord = gameWordList.get(position);
        holder.gwEditText.setText(gameWord.getWord());

        holder.wcEditText.setText(gameWord.getClue());
        if (flag == 1) {//如果是游戏的编辑页面传入，没有书名的条目、复选框、单词本图标
            holder.wcEditText.setText(gameWord.getClue());
            holder.wbTextView.setVisibility(View.GONE);
            holder.wCheckBox.setVisibility(View.GONE);
            holder.mImageViewBook.setVisibility(View.GONE);
        }
        holder.wbTextView.setText(gameWord.getBook());
        if (gameWord.isChecked()) {
            holder.wCheckBox.setChecked(true);
        } else {
            holder.wCheckBox.setChecked(false);
        }
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (holder.wcEditText.hasFocus()) {
                    mTextListener.onTextChanged(position, holder.wcEditText.getText().toString());
                }
                if (holder.wCheckBox.hasFocus()) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = holder.getAdapterPosition();
                            if (itemClickListener != null) {
                                itemClickListener.onItemClicked(position, v);
                            }
                        }
                    });
                }
            }
        };
        holder.wcEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.wcEditText.addTextChangedListener(textWatcher);
                } else {
                    holder.wcEditText.removeTextChangedListener(textWatcher);
                }
            }
        });
        holder.wCheckBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.wCheckBox.addTextChangedListener(textWatcher);
                } else {
                    holder.wCheckBox.removeTextChangedListener(textWatcher);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameWordList.size();
    }
}
/**
 * 总结一下现在gamewordlist布局的情况
 * 1.复选框是万万不可点的，点了也没作用，只会造成界面点击混乱
 * 2.复选框的实现还是点击item，但由于item有多处需要焦点的控件，
 * 所以在改变item控件各属性的情况下，要考虑点击item是否失焦
 * 3.如果出现bug先不要修改控件，如果首先修改了控件，即使bug解决了，也可能引发上面的问题
 */
