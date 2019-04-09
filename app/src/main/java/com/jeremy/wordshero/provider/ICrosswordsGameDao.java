package com.jeremy.wordshero.provider;

import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.GameWord;

import java.util.List;

/**
 * @author lxh
 */
public interface ICrosswordsGameDao {
    /**
     * 创建游戏
     * @param game
     * @return
     */
    public long createGame(Game game);

    /**
     * 列出所有游戏
     * @return
     */
    public List<Game> findAllGame();

    /**
     * 按创建时间列出所有游戏
     * @return
     */
    public List<Game> findAllGameOrderByCreateTime();

    /**
     * 插入游戏单词
     * @param gameWord
     * @return
     */
    public long insertGameWord(GameWord gameWord);

    /**
     * 通过游戏Id列出所有游戏中的单词
     * @param gameId
     * @return
     */
    public List<GameWord> findAllGameWordByGameId(int gameId);

    /**
     * 通过标题抓出游戏的id
     * @param title
     * @return
     */
    public int findGameIdByTitle(String title);

    /**
     * 通过id找到游戏
     * @param id
     * @return
     */
    public Game findGameById(int id);

    /**
     * 通过id删除游戏
     * @param id
     * @return
     */
    public long deleteGameWordByGameId(int id);

    /**
     * 通过游戏id删除所属游戏单词
     * @param gameId
     * @return
     */
    public long deleteGameById(int gameId);

    /**
     * 更新游戏
     * @param game
     * @return
     */
    public long updateGameById(Game game);

    /**
     * 更新游戏单词
     * @param word
     * @return
     */
    public long updateGameWordByGameId(GameWord word);
}
