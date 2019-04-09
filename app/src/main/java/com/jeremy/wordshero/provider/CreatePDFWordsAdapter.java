package com.jeremy.wordshero.provider;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremy.wordshero.R;
import java.util.List;

/**
 * 生成单词游戏pdf的单词adapter
 * @author  cyd
 */
public class CreatePDFWordsAdapter extends RecyclerView.Adapter<CreatePDFWordsAdapter.ViewHolder> {

    private Context mContext;
    private List<SelectWords> mWordsList;
    public CreatePDFWordsAdapter (Context context,List<SelectWords> list) {
        this.mContext = context;
        this.mWordsList = list;

    }
    @Override
    public CreatePDFWordsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(mContext==null){
            mContext=viewGroup.getContext();
        }
        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.activity_create_pdf_word_item_layout, viewGroup, false);
        final CreatePDFWordsAdapter.ViewHolder viewHolder = new CreatePDFWordsAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder itemViewHolder, final int position) {

        SelectWords wordItem = mWordsList.get(position);
        itemViewHolder.wordTv.setText(wordItem.getWords());
        if(onItemClickListener != null) {

            //删除事件
            itemViewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemViewHolder.itemView,position );
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

    public void removeItem(int position) {
        mWordsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount()-position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView wordTv;
        public ImageButton deleteItem;
        public ViewHolder(View itemView) {
            super(itemView);
            wordTv = (TextView)itemView.findViewById(R.id.create_pdf_word_item);
            deleteItem = (ImageButton)itemView.findViewById(R.id.delete_word_in_create_pdf);
        }
    }

    /**
     * 设置监听接口
     */
    public interface  onItemClickListener{
        void onItemClick(View view, int position);

    }
    private CreatePDFWordsAdapter.onItemClickListener onItemClickListener;

    public void setOnItemClickListener(CreatePDFWordsAdapter.onItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
}
