package com.jeremy.wordshero.provider;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.bean.Book;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.graphics.Typeface.createFromAsset;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private Context mContext;
    private List<Book> mBooksInfoList;
    private onItemClickListener onItemClickListener;
    private onItemLongClickListener onItemLongClickListener;
    private String className;
    private CrosswordsDaoImp crosswordsDaoImp;

    public void setItemClickListener(onItemClickListener mItemClickListener) {
        onItemClickListener = mItemClickListener;
    }

    public void setItemLongClickListener(onItemLongClickListener mItemLongClickListener) {
        onItemLongClickListener = mItemLongClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView bookName;
        TextView wordsNum;
        TextView bookIntroduction;
        CheckBox mCheckBox;
        Button editBookBtn;
        Button deleteBookBtn;
        LinearLayout linearLayout;
        TextView mCreateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
//            childCardView=(CardView)itemView.findViewById(R.id.child_card);
            bookName = (TextView) itemView.findViewById(R.id.book_name);
            wordsNum = (TextView) itemView.findViewById(R.id.words_num);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.select_check);
            bookIntroduction = (TextView) itemView.findViewById(R.id.book_introduction);
            editBookBtn = itemView.findViewById(R.id.edit_book_btn);
            deleteBookBtn = itemView.findViewById(R.id.delete_book_btn);
            linearLayout = itemView.findViewById(R.id.hidden_view_in_book);
            mCreateTime = itemView.findViewById(R.id.book_create_time);
        }
    }

    public BooksAdapter(List<Book> booksInfoList, String className) {
        mBooksInfoList = booksInfoList;
        this.className = className;
    }

    public BooksAdapter(List<Book> booksInfoList) {
        mBooksInfoList = booksInfoList;
    }

    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            crosswordsDaoImp = new CrosswordsDaoImp(mContext);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.books_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongClick(v, position);
                }
                return true;

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(BooksAdapter.ViewHolder holder, int position) {
        final Book book = mBooksInfoList.get(position);
        holder.bookName.setText(book.getName());
        holder.wordsNum.setText(crosswordsDaoImp.allWordNum(book.getId()) + "");
        holder.bookIntroduction.setText(book.getIntroduction());
        Date date = new Date();
        date.setTime(book.getCreateTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.mCreateTime.setText(simpleDateFormat.format(date));
        Typeface typeface = createFromAsset(holder.itemView.getResources().getAssets(), "fonts/orange juice 2.0.ttf");
        holder.wordsNum.setTypeface(typeface);
        if (className != "WordBooksFragment") {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {
            if (book.isCheckBoxIsShow()) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
            } else {
                holder.mCheckBox.setVisibility(View.GONE);
            }
        }
        if (book.isChecked()) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }
    }

    /**
     * 设置监听接口
     */
    public interface onItemClickListener {
        /**
         * @param view
         * @param position
         */
        public void onItemClick(View view, int position);

    }

    /**
     * 长按监听接口
     */
    public interface onItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    @Override
    public int getItemCount() {
        return mBooksInfoList.size();
    }
}

