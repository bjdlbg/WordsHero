package com.jeremy.wordshero.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jeremy.wordshero.bean.Book;
import com.jeremy.wordshero.bean.WordInfo;


import java.util.ArrayList;
import java.util.List;

public class CrosswordsDaoImp extends DataBaseAdapter implements ICrosswordsDao {
    DBHelper dbHelper;
    private long result;

    public CrosswordsDaoImp(Context context) {
        super(context);
        dbHelper = new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);

    }


    @Override
    public long addBook(Book book) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WordBookColumns.BOOK_NAME, book.getName());
        values.put(WordBookColumns.INSTRUCTION, book.getIntroduction());
        values.put(WordBookColumns.WORD_COUNT, book.getNum());
        values.put(WordBookColumns.CREATE_TIME, book.getCreateTime());
        result = db.insert(DataBaseAdapter.DB_TABLE_WORD_BOOK, null, values);
        db.close();
        return result;
    }

    @Override
    public long removeBook(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = db.delete(DB_TABLE_WORD_BOOK, WordBookColumns._ID + "=?", new String[]{id + ""});
        db.close();
        return result;
    }

    @Override
    public Book findBook(int bookId) {
        Cursor cursor = getmSQLiteDatabase().query(DB_TABLE_WORD_BOOK,
                null, WordBookColumns._ID + "=?", new String[]{bookId + ""},
                null, null, null);
        cursor.moveToNext();
        Book book = new Book();
        book.setId(cursor.getInt(WordBookColumns._ID_INDEX));
        book.setName(cursor.getString(WordBookColumns.BOOK_NAME_INDEX));
        book.setIntroduction(cursor.getString(WordBookColumns.INSTRUCTION_INDEX));
        book.setNum(cursor.getInt(WordBookColumns.WORD_COUNT_INDEX));
        book.setCreateTime(cursor.getLong(WordBookColumns.CREATE_TIME_INDEX));
        return book;
    }

    @Override
    public long updateBook(Book book) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WordBookColumns.BOOK_NAME, book.getName());
        values.put(WordBookColumns.INSTRUCTION, book.getIntroduction());
        values.put(WordBookColumns.WORD_COUNT, book.getNum());
        values.put(WordBookColumns.UPDATE_TIME, book.getUpdateTime());
        long result = db.update(DB_TABLE_WORD_BOOK, values, WordBookColumns._ID + "=?", new String[]{book.getId() + ""});
        return result;
    }

    @Override
    public List<Book> findAllBookOrderByCreateTime() {
        Cursor cursor = getmSQLiteDatabase().query(DB_TABLE_WORD_BOOK,
                null, null, null,
                null, null, WordBookColumns.CREATE_TIME + " DESC");
        List<Book> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setId(cursor.getInt(WordBookColumns._ID_INDEX));
            book.setName(cursor.getString(WordBookColumns.BOOK_NAME_INDEX));
            book.setIntroduction(cursor.getString(WordBookColumns.INSTRUCTION_INDEX));
            book.setNum(cursor.getInt(WordBookColumns.WORD_COUNT_INDEX));
            book.setCreateTime(cursor.getLong(WordBookColumns.CREATE_TIME_INDEX));
            list.add(book);
        }
        cursor.close();
        return list;
    }

    @Override
    public long addWord(WordInfo wordInfo) {
        ContentValues values = new ContentValues();
        values.put(WordColumns.WORD, wordInfo.getWord());
        values.put(WordColumns.TRANSLATION, wordInfo.getTranslation());
        values.put(WordColumns.BOOK_ID, wordInfo.getBook_id());
        values.put(WordColumns.CREATE_TIME, wordInfo.getCrate_time());
        return getmSQLiteDatabase().insert(DB_TABLE_WORD, null, values);
    }

    @Override
    public long removeAllWord(int bookId) {
        long deleteBookResult = getmSQLiteDatabase().delete(DB_TABLE_WORD, WordColumns.BOOK_ID + "=?", new String[]{bookId + ""});
        long deleteWordResult = getmSQLiteDatabase().delete(DB_TABLE_WORD_BOOK, WordBookColumns._ID + "=?", new String[]{bookId + ""});
        return deleteBookResult + deleteWordResult;
    }

    @Override
    public long removeWord(int wordId) {
        return getmSQLiteDatabase().delete(DB_TABLE_WORD, WordColumns._ID + "=?", new String[]{wordId + ""});
    }

    @Override
    public long updateWord(WordInfo wordInfo) {
        ContentValues values = new ContentValues();
        values.put(WordColumns.WORD, wordInfo.getWord());
        values.put(WordColumns.TRANSLATION, wordInfo.getTranslation());
        values.put(WordColumns.BOOK_ID, wordInfo.getBook_id());
        values.put(WordColumns.UPDATE_TIME, wordInfo.getUpdate_time());
        return getmSQLiteDatabase().update(DB_TABLE_WORD, values, WordColumns._ID + "=?", new String[]{wordInfo.getId() + ""});
    }

    @Override
    public List<WordInfo> findAllWord(int bookId) {
        Cursor cursor = getmSQLiteDatabase().query(DB_TABLE_WORD,
                null, WordColumns.BOOK_ID + "=?", new String[]{bookId + ""},
                null, null, null);
        List<WordInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            WordInfo wordInfo = new WordInfo();
            wordInfo.setId(cursor.getInt(WordColumns._ID_INDEX));
            wordInfo.setWord(cursor.getString(WordColumns.WORD_INDEX));
            wordInfo.setTranslation(cursor.getString(WordColumns.TRANSLATION_INDEX));
            wordInfo.setBook_id(cursor.getInt(WordColumns.BOOK_ID_INDEX));
            list.add(wordInfo);
        }
        cursor.close();
        return list;
    }

    @Override
    public long allWordNum(int bookId) {
        Cursor cursor = getmSQLiteDatabase().rawQuery("select count(*) from " + DB_TABLE_WORD + " where " + WordColumns.BOOK_ID + " =?", new String[]{bookId + ""});
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    @Override
    public List<WordInfo> findAllWordOrderByCreateTime(int bookId) {
        Cursor cursor = getmSQLiteDatabase().query(DB_TABLE_WORD,
                null, WordColumns.BOOK_ID + "=?", new String[]{bookId + ""},
                null, null, WordColumns.CREATE_TIME + " DESC");
        List<WordInfo> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            WordInfo wordInfo = new WordInfo();
            wordInfo.setId(cursor.getInt(WordColumns._ID_INDEX));
            wordInfo.setWord(cursor.getString(WordColumns.WORD_INDEX));
            wordInfo.setTranslation(cursor.getString(WordColumns.TRANSLATION_INDEX));
            wordInfo.setBook_id(cursor.getInt(WordColumns.BOOK_ID_INDEX));
            wordInfo.setCrate_time(cursor.getLong(WordColumns.CREATE_TIME_INDEX));
            list.add(wordInfo);
        }
        cursor.close();
        return list;
    }
}
