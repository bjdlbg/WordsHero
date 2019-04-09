package com.jeremy.wordshero.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.GameWord;


import java.util.ArrayList;
import java.util.List;

/**
 * @author lxh
 */
public class CrosswordsGameDaoImp extends DataBaseAdapter implements ICrosswordsGameDao {
    private long result;
    DBHelper dbHelper;
    public CrosswordsGameDaoImp(Context context) {
        super(context);
        dbHelper = new DBHelper(context,DATABASE_NAME,DATABASE_VERSION);
    }



    @Override
    public long createGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(GameColumns.TITLE, game.getTitle());
        values.put(GameColumns.INSTRUCTION, game.getInstruction());
        values.put(GameColumns.WORD_COUNT, game.getCount());
        values.put(GameColumns.CREATE_TIME, game.getCreateTime());
        values.put(GameColumns.UPDATE_TIME, game.getUpdateTime());
        values.put(GameColumns.OPEN_TIME, game.getOpenTime());
        values.put(GameColumns.PDF_PATH, game.getPdfPath());
        Log.d("CrosswordsGameDaoImp:", "values:" + values.toString());
        if (getmSQLiteDatabase() == null) {
            Log.d("CrosswordsGameDaoImp:", "getmSQLiteDatabase()isWrong");
        }
        result = getmSQLiteDatabase().insert(DataBaseAdapter.DB_TABLE_GAME, null, values);
        return result;
    }

    @Override
    public long updateGameById(Game game) {
        ContentValues values=new ContentValues();
        values.put(GameColumns.TITLE,game.getTitle());
        values.put(GameColumns.INSTRUCTION,game.getInstruction());
        values.put(GameColumns.UPDATE_TIME,game.getUpdateTime());
        values.put(GameColumns.OPEN_TIME,game.getOpenTime());
        return getmSQLiteDatabase().update(DB_TABLE_GAME,values,WordColumns._ID+"=?",new String[]{game.getId()+""});
    }

    @Override
    public long updateGameWordByGameId(GameWord word) {
        ContentValues values=new ContentValues();
        values.put(GameWordColumns.CLUE,word.getClue());
        return getmSQLiteDatabase().update(DB_TABLE_GAME_WORD,values,GameWordColumns.GAME_ID+"=?",new String[]{word.getGameId()+""});
    }


    @Override
    public List<Game> findAllGame() {
        Cursor cursor = getmSQLiteDatabase().rawQuery("select * from " + DB_TABLE_GAME, null);
        List<Game> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Game game = new Game();
            game.setId(cursor.getInt(GameColumns._ID_INDEX));
            game.setTitle(cursor.getString(GameColumns.TITLE_INDEX));
            game.setInstruction(cursor.getString(GameColumns.INSTRUCTION_INDEX));
            game.setCount(cursor.getInt(GameColumns.WORD_COUNT_INDEX));
            game.setCreateTime(cursor.getLong(GameColumns.CREATE_TIME_INDEX));
            game.setUpdateTime(cursor.getLong(GameColumns.UPUDATE_TIME_INDEX));
            game.setOpenTime(cursor.getLong(GameColumns.OPEN_TIME_INEDX));
            game.setPdfPath(cursor.getString(GameColumns.PDF_PATH_INDEX));
            list.add(game);
        }
        cursor.close();
        return list;
    }

    @Override
    public List<Game> findAllGameOrderByCreateTime() {
        Cursor cursor=getmSQLiteDatabase().query(DB_TABLE_GAME,
                null,null,null,
                null,null,GameColumns.CREATE_TIME+" DESC" );
        List<Game> list=new ArrayList<>();
        while(cursor.moveToNext()){
            Game game=new Game();
            game.setId(cursor.getInt(GameColumns._ID_INDEX));
            game.setTitle(cursor.getString(GameColumns.TITLE_INDEX));
            game.setInstruction(cursor.getString(GameColumns.INSTRUCTION_INDEX));
            game.setCount(cursor.getInt(GameColumns.WORD_COUNT_INDEX));
            game.setCreateTime(cursor.getLong(GameColumns.CREATE_TIME_INDEX));
            game.setUpdateTime(cursor.getLong(GameColumns.UPUDATE_TIME_INDEX));
            game.setOpenTime(cursor.getLong(GameColumns.OPEN_TIME_INEDX));
            game.setPdfPath(cursor.getString(GameColumns.PDF_PATH_INDEX));
            list.add(game);
        }
        cursor.close();
        return list;
    }

    @Override
    public long insertGameWord(GameWord gameWord) {
        ContentValues values = new ContentValues();
        values.put(GameWordColumns.WORD, gameWord.getWord());
        values.put(GameWordColumns.CLUE, gameWord.getClue());
        values.put(GameWordColumns.GAME_ID, gameWord.getGameId());
        values.put(GameWordColumns.BOOK, gameWord.getBook());
        return getmSQLiteDatabase().insert(DB_TABLE_GAME_WORD, null, values);
    }

    @Override
    public List<GameWord> findAllGameWordByGameId(int gameId) {
        Cursor cursor = getmSQLiteDatabase().query(DB_TABLE_GAME_WORD,
                null, GameWordColumns.GAME_ID + "=?", new String[]{gameId + ""},
                null, null, null);
        List<GameWord> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            GameWord gameWord = new GameWord();
            gameWord.setId(cursor.getInt(GameWordColumns._ID_INDEX));
            gameWord.setWord(cursor.getString(GameWordColumns.WORD_INDEX));
            gameWord.setClue(cursor.getString(GameWordColumns.CLUE_INDEX));
            gameWord.setGameId(cursor.getInt(GameWordColumns.GAME_ID_INDEX));
            list.add(gameWord);
        }
        cursor.close();
        return list;
    }

    @Override
    public int findGameIdByTitle(String title) {
        int id = -1;
        Cursor cursor = getmSQLiteDatabase().query(DB_TABLE_GAME, null, GameColumns.TITLE + "=?", new String[]{title + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(GameColumns._ID_INDEX);
        }
        return id;
    }

    @Override
    public Game findGameById(int id) {
        Cursor cursor=getmSQLiteDatabase().query(DB_TABLE_GAME,
                null,GameColumns._ID+"=?",new String[]{id+""},
                null,null,null);
        Game game=new Game();
        cursor.moveToFirst();
        game.setTitle(cursor.getString(GameColumns.TITLE_INDEX));
        game.setInstruction(cursor.getString(GameColumns.INSTRUCTION_INDEX));
        game.setPdfPath(cursor.getString(GameColumns.PDF_PATH_INDEX));
        return game;
    }

    @Override
    public long deleteGameWordByGameId(int id) {
        long result =  getmSQLiteDatabase().delete(DB_TABLE_GAME,GameColumns._ID+"=?",new String[]{id+""});
        return result;
    }

    @Override
    public long deleteGameById(int gameId) {
        long result =  getmSQLiteDatabase().delete(DB_TABLE_GAME_WORD,GameWordColumns._ID+"=?",new String[]{gameId+""});
        return result;
    }



}
