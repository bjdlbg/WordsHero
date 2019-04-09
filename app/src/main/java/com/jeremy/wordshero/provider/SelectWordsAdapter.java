package com.jeremy.wordshero.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.LogUtil;
import com.jeremy.wordshero.bean.WordInfo;

import java.util.List;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class SelectWordsAdapter extends RecyclerView.Adapter<SelectWordsAdapter.ViewHolder> {

    private Context mContext;
    private List<WordInfo> mWordsList;
    private static final String TAG = "SelectWordsAdapter";
    private OnTextChangeListener mTextListener;


    public SelectWordsAdapter (Context context,List<WordInfo> list) {
        this.mContext = context;
        this.mWordsList = list;

    }

    /**
     * 设置自定义接口成员变量
     * @param onTextChangeListener
     */
    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener){
        this.mTextListener=onTextChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(mContext==null){
            mContext=viewGroup.getContext();
        }
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.select_words_item_layout, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder itemViewHolder, final int position) {

        final int itemPosition = itemViewHolder.getAdapterPosition();
        final WordInfo wordItem = mWordsList.get(itemPosition);
        itemViewHolder.wordTv.setText(wordItem.getWord());

        final TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(itemViewHolder.wordTv.hasFocus()){//判断当前EditText是否有焦点在
                    //通过接口回调将数据传递到Activity中
                    mTextListener.onTextChanged(position,itemViewHolder.wordTv.getText().toString());
                }
            }
        };
        //设置EditText的焦点监听器判断焦点变化，当有焦点时addTextChangedListener，失去焦点时removeTextChangeListener
        itemViewHolder.wordTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    itemViewHolder.wordTv.addTextChangedListener(textWatcher);
                }else{
                    itemViewHolder.wordTv.removeTextChangedListener(textWatcher);
                }
            }
        });
        //显示翻译
        if (wordItem.getTranslation()!= null) {
            itemViewHolder.transaltionTv.setVisibility(View.VISIBLE);
            itemViewHolder.transaltionTv.setText(wordItem.getTranslation()+".");
            LogUtil.d(TAG,"adapter run"+wordItem.getTranslation());
        } else {
            itemViewHolder.transaltionTv.setVisibility(View.INVISIBLE);
        }

        if(onItemClickListener != null) {

            //删除事件
            itemViewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemViewHolder.itemView,itemPosition );
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (mWordsList != null) {
            return mWordsList.size();
        }
        return 0;
    }

    public void addItem(int position,WordInfo word) {
        mWordsList.add(position,word);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount()-position);

    }

    public void removeItem(int position) {
        mWordsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount()-position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //单词
        public EditText wordTv;
        //翻译
        public TextView transaltionTv;
        //删除按钮
        public ImageButton deleteItem;
        public ViewHolder(View itemView) {
            super(itemView);
            wordTv = (EditText)itemView.findViewById(R.id.select_word_item);
            transaltionTv = (TextView)itemView.findViewById(R.id.select_word_translation);
            deleteItem = (ImageButton)itemView.findViewById(R.id.delete_select_word);
        }
    }

    /**
     * 设置监听接口
     */
    public interface  onItemClickListener{
        void onItemClick(View view, int position);

    }
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

}
