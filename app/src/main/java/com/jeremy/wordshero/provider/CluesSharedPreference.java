package com.jeremy.wordshero.provider;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cyd on 19-4-5.
 */

public class CluesSharedPreference {

    /**
     * 建立存储clues的sharedPreference
     * @param context
     * @return
     */
    public static SharedPreferences getSp(Context context) {
        SharedPreferences sp = context.getSharedPreferences("clues", Context.MODE_PRIVATE);
        return sp;
    }

    /**
     * 设置对应单词线索
     * @param context
     * @param word
     * @param clue
     */
    public static void  setCluesByWord(Context context,String word,String clue) {
        SharedPreferences.Editor e = getSp(context).edit();
        e.putString(word,clue);
        e.apply();
    }

    /**
     * 得到对应单词的线索
     * @param context
     * @param word
     * @return
     */
    public static String getCluesByWord(Context context,String word) {
        return getSp(context).getString(word,"");
    }


}
