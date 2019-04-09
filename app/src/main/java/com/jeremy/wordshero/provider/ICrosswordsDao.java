package com.jeremy.wordshero.provider;


import com.jeremy.wordshero.bean.Book;
import com.jeremy.wordshero.bean.WordInfo;

import java.util.List;

public interface ICrosswordsDao {
    /**
     * 增加book所有信息
     * @param book
     */
    public long addBook(Book book);

    /**
     * 通过id删除单词
     * @param id
     */
    public long removeBook(int id);

    /**
     * 通过id查询单词本信息
     * @param bookId
     * @return
     */
    public Book findBook(int bookId);


    /**
     * 更新book所有信息
     * @param book
     */
    public long updateBook(Book book);

    /**
     * 列出所有单词本
     * @return
     */
    public List<Book> findAllBookOrderByCreateTime();

    /**
     * 添加单词所有信息
     * @param wordInfo
     */
    public long addWord(WordInfo wordInfo);

    /**
     * 通过单词本id删除所有单词
     * @param bookId
     */
    public long removeAllWord(int bookId);

    /**
     * 通过单词id查询单词
     * @param wordId
     * @return
     */
    public long removeWord(int wordId);


    /**
     * 更新单词所有信息
     * @param wordInfo
     */
    public long updateWord(WordInfo wordInfo);

    /**
     * 通过单词本id列出所有单词
     * @param bookId
     * @return
     */
    public List<WordInfo> findAllWord(int bookId);

    /**
     * 获取单词本中单词总数
     * @param bookId
     * @return
     */
    public long allWordNum(int bookId);

    /**
     * 通过单词本id按创建时间从大到小排序列出所有单词
     * @param bookId
     * @return
     */
    public List<WordInfo> findAllWordOrderByCreateTime(int bookId);
}
