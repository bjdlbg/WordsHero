package com.jeremy.wordshero.Util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查app内输入内容校验的工具类
 *
 * @author: jixiang
 * @date: On 2019/1/21
 */
public class EditChecker {
    //输入只能是英文
    public static Pattern p = Pattern.compile("^[A-Za-z]+$");

    public EditChecker() {

    }

    /**
     * 校验单词格式(只能包含英文)
     *
     * @param s
     */
    public static boolean checkWord(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    /**
     * 检验输入的内容是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.replaceAll(" ","" ).equals("")) {
            return true;
        }
        return false;
    }


}
