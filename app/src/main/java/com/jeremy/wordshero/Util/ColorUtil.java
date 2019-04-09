package com.jeremy.wordshero.Util;

import com.itextpdf.text.BaseColor;

/**
 * 颜色转换类
 */
public class ColorUtil {
    /**
     *将16进制颜色转换为8进制
     * @param colorStr e.g. "#FFFFFF"
     * @return
     */
    public static BaseColor hex2Rgb(String colorStr) {
        return new BaseColor(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }
}
