package com.jeremy.wordshero.provider;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.WordInfo;


import java.util.List;

/**
 * 编辑单词本页面的单词列表适配器.
 *
 * @author cyd
 */
public class EditBookWordsAdapter extends RecyclerView.Adapter<EditBookWordsAdapter.ViewHolder> {

    private Context mContext;
    private List<WordInfo> mWordsList;
    private static final String TAG = "EditBookWordsAdapter";
    private OnTextChangeListener mTextListener;

    public EditBookWordsAdapter(Context context, List<WordInfo> wordInfoList) {
        this.mContext = context;
        this.mWordsList = wordInfoList;
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.mTextListener = onTextChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.edit_book_words_item_layout, viewGroup, false);
        final EditBookWordsAdapter.ViewHolder viewHolder = new EditBookWordsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder itemViewHolder, final int position) {
        WordInfo wordInfo = mWordsList.get(position);
        itemViewHolder.word.setText(wordInfo.getWord());
        itemViewHolder.translation.setText(wordInfo.getTranslation());
        //Here you can fill your row view

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (itemViewHolder.word.hasFocus()) {
                    mTextListener.onTextChanged(position, itemViewHolder.word.getText().toString(), 0);
                }
                if (itemViewHolder.translation.hasFocus()) {
                    mTextListener.onTextChanged(position, itemViewHolder.translation.getText().toString(), 1);
                }
            }
        };
        itemViewHolder.translation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    itemViewHolder.translation.addTextChangedListener(textWatcher);
                } else {
                    itemViewHolder.translation.removeTextChangedListener(textWatcher);
                }
            }
        });
        itemViewHolder.word.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    itemViewHolder.word.addTextChangedListener(textWatcher);
                } else {
                    itemViewHolder.word.removeTextChangedListener(textWatcher);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextInputEditText word;
        public TextInputEditText translation;

        public ViewHolder(View itemView) {
            super(itemView);
            word = (TextInputEditText) itemView.findViewById(R.id.edit_book_word_item);
            translation = (TextInputEditText) itemView.findViewById(R.id.edit_book_word_clues_item);
        }
    }
}
