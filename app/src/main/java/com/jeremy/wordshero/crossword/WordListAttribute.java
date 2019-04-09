package com.jeremy.wordshero.crossword;

/**
 * 算法之前，单词列表属性
 * @author: J.xiang
 * @date: On 2018/12/17
 */
public class WordListAttribute {
    public static class Position {
        public int x;
        public int y;
        int direction;

        Position(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

    }

    class LetterIndex {
        public String word;
        public int index;
    }

    class SearchQueueItem {
        public int x;
        public int y;
        public char c;
        public int d;
    }
}
