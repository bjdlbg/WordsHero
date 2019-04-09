package com.jeremy.wordshero.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseAdapter {
    public static final String DATABASE_NAME = "CrossWords.db";

    public static final int DATABASE_VERSION = 2;

    public static final String DB_TABLE_WORD_BOOK = "word_book";
    public static final String DB_TABLE_WORD = "word";
    public static final String DB_TABLE_GAME = "game";
    public static final String DB_TABLE_GAME_WORD = "game_word";

    public SQLiteDatabase mSQLiteDatabase = null;
    private DBHelper mDbHelper = null;
    private Context mContext;

    public DataBaseAdapter(Context context) {
        this.mContext = context;
    }

    public static class GameColumns implements BaseColumns {
        public static final String _ID = "_id";
        public static final String TITLE = "title";
        public static final String INSTRUCTION = "instruction";
        public static final String WORD_COUNT = "word_count";
        public static final String CREATE_TIME = "create_time";
        public static final String UPDATE_TIME = "update_time";
        public static final String OPEN_TIME = "open_time";
        public static final String PDF_PATH = "pdf_path";
        public static final String[] GAME_QUERY_COLUMNS = {
                _ID, TITLE, INSTRUCTION, WORD_COUNT, CREATE_TIME, UPDATE_TIME, OPEN_TIME, PDF_PATH
        };
        public static final int _ID_INDEX = 0;
        public static final int TITLE_INDEX = 1;
        public static final int INSTRUCTION_INDEX = 2;
        public static final int WORD_COUNT_INDEX = 3;
        public static final int CREATE_TIME_INDEX = 4;
        public static final int UPUDATE_TIME_INDEX = 5;
        public static final int OPEN_TIME_INEDX = 6;
        public static final int PDF_PATH_INDEX = 7;
    }

    public static class GameWordColumns implements BaseColumns {
        public static final String _ID = "_id";
        public static final String WORD = "word";
        public static final String CLUE = "clue";
        public static final String GAME_ID = "game_id";
        public static final String BOOK ="book_name";
        public static final String[] GAME_WORD_QUERY_COLUMNS = {
                _ID, WORD, CLUE, GAME_ID, BOOK
        };
        public static final int _ID_INDEX = 0;
        public static final int WORD_INDEX = 1;
        public static final int CLUE_INDEX = 2;
        public static final int GAME_ID_INDEX = 3;
        public static final int BOOK_INDEX=4;

    }

    /**
     * 单词本表列
     */
    public static class WordBookColumns implements BaseColumns {
        public static final String _ID = "_id";
        public static final String BOOK_NAME = "book_name";
        public static final String INSTRUCTION = "instruction";
        public static final String COVER_IMAGE = "cover_image";
        public static final String WORD_COUNT = "word_count";
        public static final String CREATE_TIME = "creat_time";
        public static final String UPDATE_TIME = "update_time";

        public static final String[] WORD_BOOK_QUERY_COLUMNS = {
                _ID, BOOK_NAME, INSTRUCTION, COVER_IMAGE, WORD_COUNT, CREATE_TIME, UPDATE_TIME
        };
        public static final int _ID_INDEX = 0;
        public static final int BOOK_NAME_INDEX = 1;
        public static final int INSTRUCTION_INDEX = 2;
        public static final int COVER_IMAGE_INDEX = 3;
        public static final int WORD_COUNT_INDEX = 4;
        public static final int CREATE_TIME_INDEX = 5;
        public static final int UPUDATE_TIME_INDEX = 6;
    }

    public static class WordColumns implements BaseColumns {
        public static final String _ID = "_id";
        public static final String WORD = "word";
        public static final String TRANSLATION = "translation";
        public static final String BOOK_ID = " book_id";
        public static final String CREATE_TIME = "create_time";
        public static final String UPDATE_TIME = "update_time";
        static final String[] WORD_QUERY_COLUMNS = {
                _ID, WORD, TRANSLATION, BOOK_ID, CREATE_TIME, UPDATE_TIME
        };
        public static final int _ID_INDEX = 0;
        public static final int WORD_INDEX = 1;
        public static final int TRANSLATION_INDEX = 2;
        public static final int BOOK_ID_INDEX = 3;
        public static final int CREATE_TIME_INDEX = 4;
        public static final int UPDATE_TIME_INDEX = 5;
    }

    private static final String CREATE_TABLE_GAME = "CREATE TABLE "
            + DB_TABLE_GAME + "("
            + GameColumns._ID + " INTEGER PRIMARY KEY,"
            + GameColumns.TITLE + " TEXT UNIQUE,"
            + GameColumns.INSTRUCTION + " TEXT,"
            + GameColumns.WORD_COUNT + " INTEGER,"
            + GameColumns.CREATE_TIME + " UNSIGNED BIG INT,"
            + GameColumns.UPDATE_TIME + " UNSIGNED BIG INT,"
            + GameColumns.OPEN_TIME + " UNSIGNED BIG INT,"
            + GameColumns.PDF_PATH + " TEXT);";
    private static final String CREATE_TABLE_GAME_WORD = "CREATE TABLE "
            + DB_TABLE_GAME_WORD + "("
            + GameWordColumns._ID + " INTEGER PRIMARY KEY,"
            + GameWordColumns.WORD + " TEXT,"
            + GameWordColumns.CLUE + " TEXT,"
            + GameWordColumns.GAME_ID + " INTEGER,"
            + GameWordColumns.BOOK +" TEXT);";
    private static final String CREATE_TABLE_WORD_BOOK = "CREATE TABLE "
            + DB_TABLE_WORD_BOOK + "("
            + WordBookColumns._ID + " INTEGER PRIMARY KEY,"
            + WordBookColumns.BOOK_NAME + " TEXT UNIQUE,"
            + WordBookColumns.INSTRUCTION + " TEXT,"
            + WordBookColumns.COVER_IMAGE + " INTEGER,"
            + WordBookColumns.WORD_COUNT + " INTEGER,"
            + WordBookColumns.CREATE_TIME + " UNSIGNED BIG INT,"
            + WordBookColumns.UPDATE_TIME + " UNSIGNED BIG INT);";
    private static final String CREATE_TABLE_WORD = "CREATE TABLE "
            + DB_TABLE_WORD + "("
            + WordColumns._ID + " INTEGER PRIMARY KEY,"
            + WordColumns.WORD + " TEXT UNIQUE,"
            + WordColumns.TRANSLATION + " TEXT,"
            + WordColumns.BOOK_ID + " INTEGER,"
            + WordColumns.CREATE_TIME + " UNSIGNED BIG INT,"
            + WordColumns.UPDATE_TIME + " UNSIGNED BIG INT);";

    public boolean isOpen() {
        if (mSQLiteDatabase == null) {
            return false;
        } else {
            return true;
        }
    }

    public void open() throws SQLException {
        if (isOpen()) {
            return;
        } else {
            mDbHelper = new DBHelper(mContext, DATABASE_NAME, DATABASE_VERSION);
            mSQLiteDatabase = mDbHelper.getWritableDatabase();
        }
    }

    public SQLiteDatabase getmSQLiteDatabase() {
        open();
        return mSQLiteDatabase;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
    }

    public class DBHelper extends SQLiteOpenHelper {
        private Context mDBContext;

        public DBHelper(Context context, String name, int version) {
            super(context, name, null, version);
            mDBContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_WORD_BOOK);
            db.execSQL(CREATE_TABLE_WORD);
            //测试用?
            db.execSQL(CREATE_TABLE_GAME);
            db.execSQL(CREATE_TABLE_GAME_WORD);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                switch (newVersion) {
                    case 1:
                        onCreate(db);
                    case 2:
                        db.execSQL(CREATE_TABLE_GAME);
                        db.execSQL(CREATE_TABLE_GAME_WORD);
                        Log.d("DataBaseAdapter:","onUpgrade().isSuccessful");
                        break;
                    default:
                }
            }
        }
    }

}
