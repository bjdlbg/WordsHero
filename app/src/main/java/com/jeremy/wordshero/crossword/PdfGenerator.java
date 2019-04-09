package com.jeremy.wordshero.crossword;

import com.itextpdf.text.DocumentException;
import com.jeremy.wordshero.bean.GameWord;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: J.xiang
 * @date: On 2018/12/17
 */
public class PdfGenerator {

    List<List<WordListAttribute.LetterIndex>> letterIndices = new ArrayList<List<WordListAttribute.LetterIndex>>(26);

    List<WordListAttribute.SearchQueueItem> searchQ;
    public static LinkedList<WordListAttribute.Position> searchQueue = new LinkedList<WordListAttribute.Position>();
    public static boolean crosswordFound = false;

    public void createPdf(List<GameWord> selectGameWordList, String title, String instruction, int gameId) throws IOException, DocumentException {
        long time1 = System.currentTimeMillis();
        //单词
        final String[] words = new String[selectGameWordList.size()];
        final String[] clues = new String[selectGameWordList.size()];
        //使用for循环得到数组
        for (int i = 0; i < selectGameWordList.size(); i++) {
            words[i] = selectGameWordList.get(i).getWord();
        }
        for (int i = 0; i < selectGameWordList.size(); i++) {
            clues[i] = selectGameWordList.get(i).getClue();
        }
        ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(words));

        ArrayList<String> wordClues = new ArrayList<>(Arrays.asList(clues));

        int length = 0;
        int indexLongest = -1;
        for (int i = 0; i < words.length; i++) {
            if (wordList.get(i).length() > length) {
                indexLongest = i;
                length = wordList.get(i).length();
            }
        }

        int minLen = findMinimumLengthByLetterCount(wordList);

        if (minLen > length) {
            length = minLen;
        }

        //length = 13;
        System.out.println("The current length of the crossword is: " + length);


        String longestWord = wordList.get(indexLongest);
        String longestWordClue = wordClues.get(indexLongest);
        wordList.remove(indexLongest);
        wordClues.remove(indexLongest);

        lo = new LetterOccurances(wordList);

        int counter = 0;

        outer:
        while (!crosswordFound) {
            Crossword test = new Crossword(2 * (length + counter) - 1, 2 * (length + counter) - 1);
            test.addDashes();
            test.firstInsert(longestWord, test.width / 2, test.length / 2, 0, longestWordClue);
            test.size = length + counter;
            test.smallestX = test.width / 2;
            test.smallestY = test.length / 2;
            test.biggestX = test.width / 2 + longestWord.length();
            test.biggestY = test.length / 2 + 1;
            searchQueue.clear();
            usedWords = new ArrayList<Boolean>(wordList.size());
            for (int i = 0; i < wordList.size(); ++i) {
                usedWords.add(false);
            }
            for (int i = 0; i < longestWord.length(); ++i) {
                searchQueue.add(new WordListAttribute.Position(test.width / 2 + i, test.length / 2, 1));
            }
            if (recursiveSearch(test, wordList, wordClues, 0, wordList.size()) == 1) {
                try {
                    //游戏入口
                    test.printCrossword(title, instruction,gameId);
                } catch (IOException | DocumentException e) {
                    e.printStackTrace();
                }
                break outer;
            }

            counter++;



        }
        long time2 = System.currentTimeMillis() - time1;
        System.out.println("用时: " + time2);
    }


    /**
     * this method takes in a crossword and a list of words and tries to place all the words into the crossword.
     * It returns 1 if it was successful and modifies the grid, and returns 0, leaving the grid
     * unchanged, if it wasn't.
     */
    private static int recursiveSearch(Crossword crossword, ArrayList<String> wordList,
                                       ArrayList<String> wordClues, int queueIndex, int wordsRemaining) throws IOException, DocumentException {
        //System.out.println("-----in recursive call-----");

        Crossword crosswordCopy = new Crossword(crossword);

        if (wordsRemaining == 0) {
            //crossword.printCrossword();
            return 1;

        } else {
            for (; queueIndex < searchQueue.size(); ++queueIndex) {
                WordListAttribute.Position intersect = searchQueue.get(queueIndex);
                char letter = crossword.grid[intersect.y][intersect.x];
                //for (int currentWordIndex = 0; currentWordIndex < wordsRemaining.size(); ++currentWordIndex) {
                ArrayList<entry> validWords = lo.getListForLetter(letter);
                for (entry e : validWords) {
                    String currentWord = wordList.get(e.wordindex);

                    if (!usedWords.get(e.wordindex)) {
                        if (intersect.direction == 0) {
                            int x = intersect.x - e.letterpos;
                            int y = intersect.y;
                            if (x >= 0 && crossword.insert(currentWord, x, y, 1, 0) == 1) {
                                crossword.addToArray(currentWord, x, y, 0, wordClues.get(e.wordindex));
                                for (int i = 0; i < currentWord.length(); i++) {
                                    searchQueue.add(new WordListAttribute.Position(x + i, y, 1));
                                }
                                usedWords.set(e.wordindex, true);
                                if (recursiveSearch(crossword, wordList, wordClues, queueIndex + 1, wordsRemaining - 1) == 1) {
                                    return 1;
                                }
                                usedWords.set(e.wordindex, false);
                                for (int i = 0; i < currentWord.length(); i++) {
                                    searchQueue.removeLast();
                                }
                                // only revert spaces where word that was added
                                crossword.copyBack(crosswordCopy, x, y, currentWord.length(), 1, 0);
                            }
                        } else {
                            int x = intersect.x;
                            int y = intersect.y - e.letterpos;
                            if (y >= 0 && crossword.insert(currentWord, x, y, 0, 1) == 1) {
                                crossword.addToArray(currentWord, x, y, 1, wordClues.get(e.wordindex));
                                for (int i = 0; i < currentWord.length(); i++) {
                                    searchQueue.add(new WordListAttribute.Position(x, y + i, 0));
                                }
                                usedWords.set(e.wordindex, true);
                                if (recursiveSearch(crossword, wordList, wordClues, queueIndex + 1, wordsRemaining - 1) == 1) {
                                    return 1;
                                }
                                usedWords.set(e.wordindex, false);
                                for (int i = 0; i < currentWord.length(); i++) {
                                    searchQueue.removeLast();
                                }
                                crossword.copyBack(crosswordCopy, x, y, currentWord.length(), 0, 1);
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    private static LetterOccurances lo;
    public static ArrayList<Boolean> usedWords;

    /**
     * Checks to see if there are possibilities
     */
    public boolean noPossibilities(String word, Crossword crossword) {

        for (int i = 0; i < word.length(); i++) {
            for (int j = 0; j < crossword.grid.length; j++) {
                for (int k = 0; k < crossword.length; k++) {
                    if (String.valueOf(word.charAt(i)).equals(crossword.grid[j][k])) {

                        return false;
                    }

                }
            }
        }
        return true;
    }

    /**
     * This method finds the minimum length of the crossword according to the number of characters that need to
     * be placed, then returns this minimum length.
     */
    private static int findMinimumLengthByLetterCount(ArrayList<String> words) {

        int totalCharacterCount = 0;
        for (String s : words) {
            totalCharacterCount += s.length();
        }


        int i = 3;
        while (Math.pow(i, 2) - Math.pow(i / 2, 2) + Math.pow((i + 1) / 2, 2) < totalCharacterCount) {
            i++;
        }
        return i;
    }


}

