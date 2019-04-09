package com.jeremy.wordshero.fragment.tab;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
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
import android.widget.LinearLayout;

import com.jeremy.wordshero.R;
import com.jeremy.wordshero.Util.LogUtil;
import com.jeremy.wordshero.activity.AnswerActivity;
import com.jeremy.wordshero.activity.EditGameActivity;
import com.jeremy.wordshero.activity.SelectBookActivity;
import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.Games;
import com.jeremy.wordshero.provider.CrosswordsGameDaoImp;
import com.jeremy.wordshero.provider.GamesAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;

/**
 * @author jixiang
 * @date 2019/3/31
 */public class WordsGameFragment extends Fragment implements Games.ItemEditBtnClickListener,
        Games.ItemAnswerTvClickListener, Games.ItemShowPdfListener, Games.ItemDeleteGameListener {
    private View rootView;
    private List<Game> gamesList = new ArrayList<>();
    private List<Game> deleteGamesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout noGameLayout;
    private GamesAdapter adapter;
    private CrosswordsGameDaoImp crosswordsGameDaoImp;
    private boolean deleteActionBarShow = false;
    private final String TAG = "WordsGameFragment";
    private static Boolean EXIT_DELETE=false;
    private boolean userHelpPic = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_word_games, container, false);
        FloatingTextButton ftb = (FloatingTextButton) rootView.findViewById(R.id.floating_text_button);
        ftb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SelectBookActivity.class);
                intent.putExtra("class", "WordsGameFragment");
                startActivity(intent);
            }
        });
        crosswordsGameDaoImp = new CrosswordsGameDaoImp(getContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.games_recycler_view);
        noGameLayout =(LinearLayout)rootView.findViewById(R.id.nogame_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter(gamesList);

        //当没有数据时候显示没有数据
        if (initGamesList()){
            recyclerView.setVisibility(View.VISIBLE);
            noGameLayout.setVisibility(View.GONE);
        }else {
            recyclerView.setVisibility(View.GONE);
            noGameLayout.setVisibility(View.VISIBLE);
        }

        //实现接口
        adapter.setItemEditBtnClickListener(this);
        adapter.setItemAnswerTvClickListener(this);
        adapter.setItemClickListener(this);
        adapter.setItemDeleteGameLister(this);
        recyclerView.setAdapter(adapter);


//        recyclerView = (RecyclerView) rootView.findViewById(R.id.games_recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new GamesAdapter(gamesList);
//        //实现接口
//        adapter.setItemEditBtnClickListener(this);
//        adapter.setItemAnswerTvClickListener(this);
//        adapter.setItemClickListener(this);
//        adapter.setItemDeleteGameLister(this);
//        recyclerView.setAdapter(adapter);
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        //在Fragment中显示menu
        setHasOptionsMenu(true);
    }

    /**
     * Edit按钮点击事件
     **/
    @Override
    public void onItemEditBtnClick(int position) {
        Intent intent = new Intent(getContext(), EditGameActivity.class);
        intent.putExtra("gameId", gamesList.get(position).getId());
        startActivity(intent);
    }

    /**
     * Answer点击事件
     */
    @Override
    public void onItemAnswerTvClick(int position, View view, String title) {
        //通过intent将游戏标题传递给AnswerActivity
        Intent intent = new Intent(getContext(), AnswerActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("gameId", gamesList.get(position).getId());
        //元素共享
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), view, "game_title");
        startActivity(intent, options.toBundle());
    }

    /**
     * item点击事件
     */
    @Override
    public void onItemClick(int position) {
        if (!deleteActionBarShow) {
            String pdfPath = gamesList.get(position).getPdfPath();
            LogUtil.d("filePath", "  pdfPath:" + pdfPath);
            File pdfFile = new File(pdfPath);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getContext(),
                    "com.luckyxmobile.CrossWords.fileprovider", pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            getActivity().startActivity(intent);
        } else {
            Game game = gamesList.get(position);
            if (game.isCheckBoxIsSelected()) {
                game.setCheckBoxIsSelected(false);
                deleteGamesList.remove(game);
            } else {
                game.setCheckBoxIsSelected(true);
                game.setPosition(position);
                deleteGamesList.add(game);
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemLongClick(int position) {
        deleteActionBarShow = true;
        for (Game game : gamesList) {
            game.setCheckBoxIsShow(true);
        }
        gamesList.get(position).setCheckBoxIsSelected(true);
        gamesList.get(position).setPosition(position);
        deleteGamesList.add(gamesList.get(position));
        adapter.notifyDataSetChanged();
        //主动触发onCreateOptionsMenu()方法的调用
        invalidateOptionsMenu(getActivity());
    }


    private boolean initGamesList() {
        Log.d(TAG, "initGamesList");
        gamesList = crosswordsGameDaoImp.findAllGameOrderByCreateTime();
        if (gamesList.isEmpty()){
            Log.d("game_data", "initGamesList: 当前没有游戏数据");
            return userHelpPic=false;
        }else{
            Log.d("game_data", "initGamesList: 有游戏数据");
            return userHelpPic=true;
        }
    }

    /**
     * 当从makeGame页面再次回到此页面时，重新加载数据库
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        //当重回游戏界面的时候再次判断是否有数据
        recyclerView = (RecyclerView) rootView.findViewById(R.id.games_recycler_view);
        noGameLayout =(LinearLayout)rootView.findViewById(R.id.nogame_layout);

        if(EXIT_DELETE) {
            EXIT_DELETE=false;
        }else{
            if (initGamesList()){
                recyclerView.setVisibility(View.VISIBLE);
                noGameLayout.setVisibility(View.GONE);
            }else {
                recyclerView.setVisibility(View.GONE);
                noGameLayout.setVisibility(View.VISIBLE);
            }
        }
        Log.d("onResume", "i am run");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GamesAdapter(gamesList);
        //实现接口
        adapter.setItemEditBtnClickListener(this);
        adapter.setItemAnswerTvClickListener(this);
        adapter.setItemClickListener(this);
        adapter.setItemDeleteGameLister(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_game, menu);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (deleteActionBarShow) {
            menu.findItem(R.id.game_delete).setVisible(true);
            actionBar.setDisplayHomeAsUpEnabled(true);//使取消按钮显示出来
        } else {
            menu.findItem(R.id.game_delete).setVisible(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.game_delete:
                showDeleteDialog();
                break;
            case android.R.id.home:
                deleteActionBarShow = false;
                invalidateOptionsMenu(getActivity());
                for (Game game : gamesList) {
                    game.setCheckBoxIsShow(false);
                    game.setCheckBoxIsSelected(false);
                }
                EXIT_DELETE=true;
                onResume();
                break;
            default:
        }
        return true;
    }

    //当删除时显示提示框
    private void showDeleteDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.confirm_to_delete_game)
                .setTitle(R.string.delete_game)
                .setIcon(R.drawable.ic_delete_gray_24dp)
                .setPositiveButton(R.string.delete_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Game game : deleteGamesList) {
                            String pdfPath = game.getPdfPath();
                            LogUtil.d("filePath", "  pdfPath:" + pdfPath);
                            File pdfFile = new File(pdfPath);
                            if (pdfFile.exists()) {
                                pdfFile.delete();
                            }
                            //即使文件不存在，数据库也要将游戏删除
                            crosswordsGameDaoImp.deleteGameWordByGameId(game.getId());
                            if (crosswordsGameDaoImp.deleteGameById(game.getId()) > 0) {
                                gamesList.remove(game.getPosition());
                                adapter.notifyItemRemoved(game.getPosition());
                                adapter.notifyItemRangeChanged(game.getPosition(), gamesList.size() - 1);
                            }
                        }
                        deleteActionBarShow = false;
                        invalidateOptionsMenu(getActivity());
                        for (Game game : gamesList) {
                            game.setCheckBoxIsShow(false);
                            game.setCheckBoxIsSelected(false);
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

    public boolean onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (deleteActionBarShow) {
            deleteActionBarShow = false;
            invalidateOptionsMenu(getActivity());
            for (Game game : gamesList) {
                game.setCheckBoxIsShow(false);
                game.setCheckBoxIsSelected(false);
            }
            EXIT_DELETE=true;
            onResume();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if(deleteActionBarShow){
            EXIT_DELETE=true;
        }
        outState.putParcelableArrayList("list_save", (ArrayList<Game>) gamesList);
        Log.d(TAG, "onSaveInstanceState");
    }

}
