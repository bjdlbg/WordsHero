package com.jeremy.wordshero.Util;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 翻译工具类
 * @author cyd
 */
public class TransUtil {
    private static final String APP_ID = "20181130000241473";
    private static final String SECURITY_KEY = "WZ01DPwwyykSqw09D0d0";

    //不翻译
    public static final String LANG_NONE = "None";
    //支持翻译的语言类型
    //中文
    public static final String LANG_ZH = "zh";
    //日语
    public static final String LANG_JP = "jp";
    //法语
    public static final String LANG_FRA = "fra";
    //西班牙语
    public static final String LANG_SPA = "spa";
    //韩语
    public static final String LANG_KOR = "kor";
    //俄语
    public static final String LANG_RU = "ru";
    //葡萄牙语
    public static final String LANG_PT = "pt";
    //德语
    public static final String LANG_DE = "de";
    //意大利语
    public static final String LANG_IT = "it";


    /**
     * 封装调用翻译接口所需信息封装后的api
     */
    public static TransApi api;

    public static TransApi getTransApi() {
        if (api != null) {
            return api;
        }
        api = new TransApi(APP_ID, SECURITY_KEY);
        return api;
    }

    /**
     *封装获取翻译结果的方法
     * @param queryWord 要翻译的单词
     * @param to 翻译语言类型
     * @return
     */
    public static String getResultTranslation(String queryWord,String to) {
        TransApi api = getTransApi();
        String resultJson = api.getTransResult(queryWord, "auto", to);
        //正则匹配，百度翻译完传回来一个json形式，包括很多内容，我只取翻译结果

        String questionRegex = "\"dst\":\"(.*)\"";
        Pattern pattern = Pattern.compile(questionRegex);
        Matcher matcher = pattern.matcher(resultJson);
        if (matcher.find()) {
            String getword = matcher.group(1);
            //API返回的是Unicode编码，import org.apache.commons.lang.StringEscapeUtils.
            //调用StringEscapeUtils.unescapeJava()方法可解决编码问题
            String newgetword = StringEscapeUtils.unescapeJava(getword);
            return newgetword;
        }
        return null;
    }

}
