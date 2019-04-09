package com.jeremy.wordshero.fragment.tab;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.EditChecker;
import com.jeremy.wordshero.activity.EditBookActivity;
import com.jeremy.wordshero.activity.MainActivity;
import com.jeremy.wordshero.activity.WordsListActivity;
import com.jeremy.wordshero.bean.Book;
import com.jeremy.wordshero.provider.BooksAdapter;
import com.jeremy.wordshero.provider.CrosswordsDaoImp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;

/**
 * @author jixiang
 * @date 2019/3/31
 */
public class WordBooksFragment extends Fragment {
    private static final String TAG = "WordBooksFragment";
    public static final int GET_BOOK_NAME = 1;
    private List<Book> bookNameList = new ArrayList<>();
    private List<Book> deleteBookList = new ArrayList<>();//要删除的单词本列表
    private String bookName;
    private BooksAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout nobookLayout;
    private View rootView;
    private CrosswordsDaoImp crosswordsDaoImp;
    private boolean deleteActionBarShow = false;
    private static Boolean EXIT_DELETE = false;
    private boolean noBookpic = false;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_word_books, container, false);
        ButterKnife.bind(this, rootView);
        crosswordsDaoImp = new CrosswordsDaoImp(getContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        //没有单词本的时候显示的布局
        nobookLayout = (LinearLayout) rootView.findViewById(R.id.nobook_layout);
        if (initBookInfo()){
            recyclerView.setVisibility(View.VISIBLE);
            nobookLayout.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            nobookLayout.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Log.d(TAG, "onCreateView run");
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化数据放在onCreate方法中防止重复添加数据，onCreate()方法只会在第一次显示fragment时运行
        //fragment会先执行 onCreate(),在执行onCreateView 和 onResume
        //onCreateView 和 onResume 在每次显示fragment时都会执行

        //没有数据时，显示无数据界面

        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate run");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume run");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        //没有单词本的时候显示的布局
        nobookLayout = (LinearLayout) rootView.findViewById(R.id.nobook_layout);
        //没有数据时，显示无数据界面

        if (EXIT_DELETE) {
            EXIT_DELETE = false;
        } else {
            if (initBookInfo()){
                recyclerView.setVisibility(View.VISIBLE);
                nobookLayout.setVisibility(View.GONE);
            }else {
                recyclerView.setVisibility(View.GONE);
                nobookLayout.setVisibility(View.VISIBLE);
            }
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BooksAdapter(bookNameList, "WordBooksFragment");
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new BooksAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (!deleteActionBarShow) {
                    Book book = bookNameList.get(position);
                    Log.d("WordsBooksFragment", "RecyclerView.getWidth()" + recyclerView.getLayoutManager().getChildAt(position).getWidth());
                    //单词列表的activity
                    Intent intent = new Intent(getContext(), WordsListActivity.class);
                    intent.putExtra(MainActivity.BOOK_ID, book.getId());
                    startActivity(intent);
                } else {
                    Book book = bookNameList.get(position);
                    if (book.isChecked()) {
                        book.setChecked(false);
                        deleteBookList.remove(book);
                    } else {
                        book.setChecked(true);
                        book.setPosition(position);
                        deleteBookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter.setItemLongClickListener(new BooksAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                deleteActionBarShow = true;
                for (Book book : bookNameList) {
                    book.setCheckBoxIsShow(true);
                }
                bookNameList.get(position).setChecked(true);
                bookNameList.get(position).setPosition(position);
                deleteBookList.add(bookNameList.get(position));
                adapter.notifyDataSetChanged();
                //主动触发onCreateOptionsMenu()方法的调用
                invalidateOptionsMenu(getActivity());
            }
        });

    }

    private boolean initBookInfo() {
        //获取单词本数据，并判断是否为空
        bookNameList = crosswordsDaoImp.findAllBookOrderByCreateTime();
        if (bookNameList.isEmpty()){
            return noBookpic=false;
        }else{
            return noBookpic=true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_book, menu);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (deleteActionBarShow) {
            menu.findItem(R.id.action_delete_book).setVisible(true);
            actionBar.setDisplayHomeAsUpEnabled(true);//使取消按钮显示出来
        } else {
            menu.findItem(R.id.action_delete_book).setVisible(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addBookDialog();
                break;
            case R.id.action_delete_book:
                showDeleteDialog();
                break;
            case android.R.id.home:
                deleteActionBarShow = false;
                invalidateOptionsMenu(getActivity());
                for (Book book : bookNameList) {
                    book.setCheckBoxIsShow(false);
                    book.setChecked(false);
                }
                EXIT_DELETE = true;
                onResume();
                break;
            default:
        }
        return true;
    }

    private void showDeleteDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.confirm_to_delete_book)
                .setTitle(R.string.delete_book)
                .setIcon(R.drawable.ic_delete_gray_24dp)
                .setPositiveButton(R.string.delete_book, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Book book : deleteBookList) {
                            crosswordsDaoImp.removeAllWord(book.getId());
                            if (crosswordsDaoImp.removeBook(book.getId()) > 0) {
                                bookNameList.remove(book.getPosition());
                                adapter.notifyItemRemoved(book.getPosition());
                                adapter.notifyItemRangeChanged(book.getPosition(), bookNameList.size() - 1);
                            }
                        }
                        deleteActionBarShow = false;
                        invalidateOptionsMenu(getActivity());
                        for (Book book : bookNameList) {
                            book.setCheckBoxIsShow(false);
                            book.setChecked(false);
                        }
                        onResume();
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

    private void addBookDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View editWordsDialog = layoutInflater.inflate(R.layout.add_book_dialog, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle(R.string.add_word_book);
        builder.setView(editWordsDialog);

        //获得控件
        final EditText bookNameET = (EditText) editWordsDialog.
                findViewById(R.id.add_book_text);
        bookNameET.setHint(R.string.book_title);
        final EditText bookInstructionET = (EditText) editWordsDialog.
                findViewById(R.id.add_instruction_text);
        bookInstructionET.setHint(R.string.book_instruction);
        //设置对话框的确定，取消事件
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得输入的字符串
                final String bookName = bookNameET.getText().toString();
                final String bookInstruction = bookInstructionET.getText().toString();
                //判断名字是否为空
                if (!EditChecker.isEmpty(bookName)) {
                    //把用户输入的数据传入对象
                    Book book = new Book(bookName, bookInstruction);
                    book.setCreateTime(System.currentTimeMillis());
                    //存入数据库
                    if (crosswordsDaoImp.addBook(book) > 0) {
                        Toast.makeText(getContext(), getString(R.string.add_word_book_success),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), getString(R.string.add_word_book_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.empty_title), Toast.LENGTH_SHORT).show();
                }
                onResume();

                try {
                    Field field = dialog.getClass().
                            getSuperclass().
                            getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().
                            getSuperclass().
                            getDeclaredField("mShowing");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GET_BOOK_NAME:
                if (resultCode == RESULT_OK) {

                }
                break;
            default:
        }
    }

    /**
     * fragment中的返回键
     * 默认返回false,交给Activity处理
     * 返回true:执行fragment中执行的逻辑
     * 返回false:执行activity中onBackPressed
     *
     * @return
     */

    public boolean onBackPressed() {
        if (deleteActionBarShow) {
            deleteActionBarShow = false;
            invalidateOptionsMenu(getActivity());
            for (Book book : bookNameList) {
                book.setCheckBoxIsShow(false);
                book.setChecked(false);
            }
            EXIT_DELETE = true;
            onResume();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if (deleteActionBarShow) {
            EXIT_DELETE = true;
        }
    }
}

