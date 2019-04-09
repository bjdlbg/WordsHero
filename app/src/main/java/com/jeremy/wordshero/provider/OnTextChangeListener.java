package com.jeremy.wordshero.provider;

/**
 * @author lxh
 */
public interface OnTextChangeListener {
    /**
     * 当EditView改变字符时触发
     * @param pos
     * @param str
     */
    void onTextChanged(int pos, String str);

    /**
     * 当同一个item中多个不同类型的EditView改变字符是触发
     * @param pos
     * @param str
     * @param flag
     */
    void onTextChanged(int pos, String str, int flag);
}
