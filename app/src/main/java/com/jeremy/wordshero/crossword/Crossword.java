package com.jeremy.wordshero.crossword;

import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jeremy.wordshero.Util.SdCardUtils;
import com.jeremy.wordshero.activity.MainActivity;
import com.jeremy.wordshero.bean.Game;
import com.jeremy.wordshero.bean.GameWord;
import com.jeremy.wordshero.provider.CrosswordsGameDaoImp;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 游戏算法以及创建PDF主类
 *
 * @author cyd
 * @author
 * @date 2018.12.18  添加获取每个单词的序号和首字母并打印在格子上
 * @date
 */
public class Crossword {

    public char[][] grid;
    public int size; //12
    public int length; //23
    public int width; //13
    public int smallestY;
    public int biggestY;
    public int smallestX;
    public int biggestX;
    public int insertCount = 0;
    public File file;


    public static int CREATEGAME = -1;
    //打印序号和首字母的字体
    private static Font italicCellFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.ITALIC);
    //打印标题的字体 ps:随便选的
//    private static Font headTitleFont = FontFactory.getFont(FontFactory.COURIER, 25, Font.BOLD);
//    private static Font headInstructionFont = FontFactory.getFont(FontFactory.COURIER, 15, Font.NORMAL);
//    private static Font cluesTitleFont = FontFactory.getFont(FontFactory.COURIER, 17, Font.BOLD);
//    private static Font cluesContentFont = FontFactory.getFont(FontFactory.COURIER, 14, Font.NORMAL);


    private static BaseFont chinese;
    static {
        try {
            chinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//            chinese = BaseFont.createFont("MHei-Medium", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
    private static Font headTitleFont=new Font(chinese,25);
    private static Font headInstructionFont=new Font(chinese,15);
    private static Font cluesTitleFont=new Font(chinese,17);
    private static Font cluesContentFont=new Font(chinese,14);



    //亮灰色
    private static BaseColor cellColor = new BaseColor(245, 245,245 );


    //格子的宽高
    private static final float CELL_WIDTH = 30;
    private static final float CELL_HEIGHT = 30;
    private CrosswordsGameDaoImp crosswordsGameDaoImp;


    /**
     * 存储了插入的每个单词和对应的坐标和方向
     */
    public ArrayList<WordPos> wordPosList = new ArrayList<>();

    Crossword(int length, int width) throws IOException, DocumentException{
        grid = new char[length][width];
        this.length = length;
        this.width = width;
    }

    Crossword(Crossword toBeCopied)  throws IOException, DocumentException{
        this.length = toBeCopied.length;
        this.width = toBeCopied.width;
        this.biggestX = toBeCopied.biggestX;
        this.biggestY = toBeCopied.biggestY;
        this.smallestX = toBeCopied.smallestX;
        this.smallestY = toBeCopied.smallestY;

        this.grid = new char[length][width];
        for (int i = 0; i < toBeCopied.grid.length; ++i) {
            for (int j = 0; j < toBeCopied.grid[i].length; ++j) {
                this.grid[i][j] = toBeCopied.grid[i][j];
            }
        }

    }


    /**
     * 创建pdf方法
     *
     */
    public void printCrossword(String title, String instruction,int gameId)
            throws IOException, DocumentException {

        file = new File(Environment.getExternalStorageDirectory()+SdCardUtils.FILE_DIR+SdCardUtils.PDF_DIR,
                title + ".PDF");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        crosswordsGameDaoImp=new CrosswordsGameDaoImp(MainActivity.getContext());


        PdfPTable table = new PdfPTable(biggestX - smallestX);
        table.setTotalWidth((biggestX - smallestX) * CELL_WIDTH);
        table.setLockedWidth(true);
        //填字格
        PdfPCell whiteCell = new PdfPCell(new Paragraph(" "));
        whiteCell.setMinimumHeight(CELL_HEIGHT);
        whiteCell.setBorderColor(BaseColor.GRAY);
        whiteCell.setBackgroundColor(cellColor);

        //非填字格
        PdfPCell blackCell = new PdfPCell(new Paragraph(" "));
        blackCell.setBackgroundColor(BaseColor.WHITE);
//        blackCell.setBorderColor(BaseColor.DARK_GRAY);
        blackCell.setMinimumHeight(CELL_HEIGHT);
//        blackCell.disableBorderSide(15);
        blackCell.setBorder(PdfPCell.NO_BORDER);


        for (int rowCount = smallestY; rowCount < biggestY; rowCount++) {

            for (int columnCount = smallestX; columnCount < biggestX; columnCount++) {
                //如果没有字母就不打印，空过去
                if (String.valueOf(grid[rowCount][columnCount]).equals("-")) {
                    table.addCell(blackCell);
                } else {
                    //此格子是否是序号格
                    boolean isNumber = false;
                    //记录要打印的序号
                    int number = 0;
                    //WordPosList存储了插入的每个单词的word，首字母的横坐标，纵坐标，单词在crossword中的排列方向
                    //遍历WordPosList中的每个单词，若单词的x,y坐标与当前打印的格子坐标吻合，则把这个格子打印上序号和首字母
                    for (int i = 0; i < wordPosList.size(); i++) {
                        WordPos wordPos = wordPosList.get(i);
                        if (wordPos.x == columnCount && wordPos.y == rowCount) {
                            number = i + 1;
                            //获取首字母,现在只打印序号，首字母以后让用户选择
//                            char headWord = wordPos.word.charAt(0);
//                            Paragraph numberAndWord = new Paragraph(number+"."+String.valueOf(headWord).toUpperCase(),italicCellFont);
                            isNumber = true;
                            table.addCell(getNumberCell(number));
//                            Log.d("hhhhhhhhhhhhhhhhhhhhh", "第" + number + "个单词:"
//                                    + wordPos.word + " 的首字母是:" + wordPos.word.charAt(0));
                            Log.d("hhhhhhhhhhhhhhhhhhhhh", "第" + number + "个单词:"
                                    + wordPos.word + " 的线索是\n" + wordPos.clue);

                            break;
                        }
                    }
                    //如果不是序号格 则打印空白格
                    if (!isNumber) {
                        table.addCell(whiteCell);
                    }
                }
            }
        }
        //纸张大小为A4纸
        Document document = new Document(PageSize.A4,
                50,
                50,
                50,
                50);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.newPage();


        //定义标题
        Chunk titleChuck = new Chunk(title + "\n\n\n", headTitleFont);
        titleChuck.setWordSpacing(1);
        //定义介绍
        Chunk instructionChuck = new Chunk(instruction + "\n\n\n", headInstructionFont);
        Paragraph titleInstruction = new Paragraph();
        //添加标题 介绍
        titleInstruction.add(titleChuck);
        titleInstruction.add(instructionChuck);
        titleInstruction.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(titleInstruction);
        document.add(table);



        //线索的表格
//        PdfPTable cluesTable = new PdfPTable(2);
//
//        cluesTable.setSpacingBefore(50);
//        cluesTable.setTotalWidth((2) * CELL_WIDTH);
//        table.setLockedWidth(false);
//        //填字格
//        Paragraph acrossTitle = new Paragraph("Across:", cluesTitleFont);
//        acrossTitle.setAlignment(Paragraph.ALIGN_CENTER);
//        PdfPCell acrossTitleCell = new PdfPCell(acrossTitle);
////        acrossTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        acrossTitleCell.setPaddingLeft(50)
//        acrossTitleCell.setBorderColor(BaseColor.WHITE);
//        acrossTitleCell.setMinimumHeight(CELL_HEIGHT);
//        cluesTable.addCell(acrossTitleCell);
//
//        Paragraph downTitle = new Paragraph("Down:", cluesTitleFont);
//        downTitle.setAlignment(Paragraph.ALIGN_CENTER);
//        PdfPCell downTitleCell = new PdfPCell(downTitle);
////        downTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        downTitleCell.setPaddingLeft(50);
//        downTitleCell.setBorderColor(BaseColor.WHITE);
//        downTitleCell.setMinimumHeight(CELL_HEIGHT);
//        cluesTable.addCell(downTitleCell);

        //横着的线索表格
        PdfPTable acrossTable = new PdfPTable(1);
        acrossTable.setSpacingBefore(50);
        acrossTable.setTotalWidth(CELL_WIDTH);
        acrossTable.setLockedWidth(false);

        Paragraph acrossTitle = new Paragraph("Across:", cluesTitleFont);
        acrossTitle.setAlignment(Paragraph.ALIGN_CENTER);
        PdfPCell acrossTitleCell = new PdfPCell(acrossTitle);
        acrossTitleCell.setMinimumHeight(CELL_HEIGHT);
        acrossTitleCell.setBorderColor(BaseColor.WHITE);
        acrossTitleCell.setPaddingBottom(15);
        acrossTitleCell.setPaddingLeft(30);
        acrossTable.addCell(acrossTitleCell);



        //竖着的线索的表格
        PdfPTable downTable = new PdfPTable(1);
        downTable.setSpacingBefore(10);
        downTable.setTotalWidth(CELL_WIDTH);
        downTable.setLockedWidth(false);

        Paragraph downTitle = new Paragraph("Down:", cluesTitleFont);
        downTitle.setAlignment(Paragraph.ALIGN_CENTER);
        PdfPCell downTitleCell = new PdfPCell(downTitle);
        downTitleCell.setMinimumHeight(CELL_HEIGHT);
        downTitleCell.setBorderColor(BaseColor.WHITE);
        downTitleCell.setPaddingBottom(15);
        downTitleCell.setPaddingLeft(30);
        downTable.addCell(downTitleCell);



        for (int i = 0; i < wordPosList.size(); i++) {
            WordPos wordPos = wordPosList.get(i);
            int number = i + 1;
            if (wordPos.d == 0) {
                //d表示单词的方法 0 ：横着 1： 竖着
                acrossTable.addCell(getCluesCell(number, wordPos.clue));


            } else {

                downTable.addCell(getCluesCell(number, wordPos.clue));

            }
        }
        //将线索表格添加到PDF
        document.add(acrossTable);
        document.add(downTable);



        document.close();
        if (gameId != CREATEGAME) {
            //更新游戏
            updateGameById(gameId, title, instruction);
            updateGameWordToDatabase(gameId);
        } else {
            insertGameToDatabase(title, instruction);
            insertGameWordToDatabase(title);
        }

//        for (int i = smallestY; i < biggestY; i++) {
//            for (int j = smallestX; j < biggestX; j++) {
//                System.out.print(grid[i][j] + "  ");
//            }
//            System.out.println();
//        }
//        //终端输出情况用于调试
//        System.out.println();
//        System.out.println("行数： " + (biggestY - smallestY));
//        System.out.println("列数数： " + (biggestX - smallestX));
//        System.out.println("smallestX: " + smallestX);
//        System.out.println("smallestY: " + smallestY);
//        System.out.println("biggestX: " + biggestX);
//        System.out.println("biggestY: " + biggestY);
    }
    public void insertGameToDatabase(String title, String instruction) {
        Game game = new Game();
        game.setTitle(title);
        game.setInstruction(instruction);
        game.setCount(wordPosList.size());
        game.setCreateTime(System.currentTimeMillis());
        game.setUpdateTime(System.currentTimeMillis());
        game.setOpenTime(System.currentTimeMillis());
        File file = new File(Environment.getExternalStorageDirectory()+ SdCardUtils.FILE_DIR+SdCardUtils.PDF_DIR,
                title + ".PDF");
        game.setPdfPath(file.toString());
        if (crosswordsGameDaoImp.createGame(game) != -1) {
//            Toast.makeText(this, "CreateGameSuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    //更新游戏的单词
    public void updateGameWordToDatabase(int gameId) {
        for (WordPos wp  : wordPosList) {
            GameWord gameWord=new GameWord();
            gameWord.setWord(wp.word);
            gameWord.setClue(wp.clue);
            gameWord.setGameId(gameId);
            crosswordsGameDaoImp.updateGameWordByGameId(gameWord);
        }
    }

    //将crossword的单词插入到数据库中
    public void insertGameWordToDatabase(String title) {
        for (WordPos wp  : wordPosList) {
            GameWord gameWord=new GameWord();
            gameWord.setWord(wp.word);
            gameWord.setClue(wp.clue);
            gameWord.setGameId(crosswordsGameDaoImp.findGameIdByTitle(title));
            crosswordsGameDaoImp.insertGameWord(gameWord);
        }
    }

    public void updateGameById(int gameId,String title,String instruction) {
        Game game = new Game();
        game.setId(gameId);
        game.setTitle(title);
        game.setInstruction(instruction);
        game.setCount(wordPosList.size());
        game.setCreateTime(System.currentTimeMillis());
        game.setUpdateTime(System.currentTimeMillis());
        game.setOpenTime(System.currentTimeMillis());
        File file = new File(Environment.getExternalStorageDirectory()+SdCardUtils.FILE_DIR+SdCardUtils.PDF_DIR,
                title + ".PDF");
        game.setPdfPath(file.toString());
        if (crosswordsGameDaoImp.updateGameById(game) != -1) {
//            Toast.makeText(this, "CreateGameSuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * length of two crosswords must be EQUAL
     */
    void copyBack(Crossword back, int x, int y, int length, int xMultiplier, int yMultiplier) {
        for (int i = 0; i < length; i++) {
            grid[y + i * yMultiplier][x + i * xMultiplier] = back.grid[y + i * yMultiplier][x + i * xMultiplier];
        }
        biggestX = back.biggestX;
        biggestY = back.biggestY;
        smallestX = back.smallestX;
        smallestY = back.smallestY;
    }


    /**
     * 记录 插入的单词中  每个单词首字母的坐标和方向
     *
     * @param word 插入的单词
     * @param x    单词(首字母)的横坐标
     * @param y    单词(首字母)的纵坐标
     * @param d    单词的方向 横向为0  纵向为1
     * @author cyd
     */
    public void addToArray(String word, int x, int y, int d, String clue) {
        boolean flag = false;
        for (int i = 0; i < wordPosList.size(); i++) {

            if (wordPosList.get(i).word.equals(word)) {
                wordPosList.get(i).x = x;
                wordPosList.get(i).y = y;
                wordPosList.get(i).d = d;
                wordPosList.get(i).clue = clue;

                flag = true;
                break;
            }
        }
        WordPos wp = new WordPos();
        if (!flag) {
            wp.word = word;
            wp.x = x;
            wp.y = y;
            wp.d = d;
            wp.clue = clue;
            wordPosList.add(wp);
            System.out.println(word + "坐标为(" + x + "," + y + ").");
        }
    }

    //第一次插入
    void firstInsert(String word, int x, int y, int d, String clue) {

        if (d == 0) {

            for (int i = 0; i < word.length(); i++) {
                grid[y][x + i] = word.charAt(i);
            }
            WordPos firstWord = new WordPos();
            firstWord.word = word;
            firstWord.x = x;
            firstWord.y = y;
            firstWord.clue = clue;
            //horizontalWords.add(firstWord);
            addToArray(word, x, y, d, clue);

        }

    }

    /**
     * this method takes in a word,a x value, a y value, and a direction (vertical or horizontal) It checks to see if that word
     * can be placed at that x/y value and in that direction. If it can, it places the word and returns 1. Else, it returns 0,
     * and it doesn't place the word.
     */
    int insert(String word, int x, int y, int xMultiplier, int yMultiplier) {

        if (x + word.length() * xMultiplier - smallestX > size || y + word.length() * yMultiplier - smallestY > size || biggestX - x > size
                || biggestY - y > size) {
            return 0;
        }
        if (!checkSurroundings(word, x, y, xMultiplier, yMultiplier)) {
            return 0;
        }
        //word is going off the grid
        if ((x + word.length() * xMultiplier > width || y + word.length() * yMultiplier > length)) {
            return 0;
        }

        for (int i = 0; i < word.length(); i++) {
            //If the next space is not a dash or the same letter
            if (!(word.charAt(i) == grid[y + i * yMultiplier][x + i * xMultiplier]
                    || grid[y + i * yMultiplier][x + i * xMultiplier] == '-')) {
                return 0;
            }

            if (grid[y + i * yMultiplier][x + i * xMultiplier] == '-') {
                if (yMultiplier == 0 && y > 0 || xMultiplier == 0 && x > 0) {
                    //check above
                    if (!(grid[y + (yMultiplier == 0 ? -1 : i)][x + (xMultiplier == 0 ? -1 : i)] == '-')) {
                        return 0;
                    }
                }
                if (yMultiplier == 0 && y < length - 1 || xMultiplier == 0 && x < length - 1) {
                    //check below
                    if (!(grid[y + (yMultiplier == 0 ? 1 : i)][x + (xMultiplier == 0 ? 1 : i)] == '-')) {
                        return 0;
                    }
                }
            }
        }

        for (int i = 0; i < word.length(); i++) {
            //please finish
            grid[y + i * yMultiplier][x + i * xMultiplier] = word.charAt(i);
        }

        if (xMultiplier == 0) {
            if (y < smallestY) {
                smallestY = y;
            }
            if (y + word.length() > biggestY) {
                biggestY = y + word.length();
            }
        } else {
            if (x < smallestX) {
                smallestX = x;
            }
            if (x + word.length() > biggestX) {
                biggestX = x + word.length();
            }
        }


        return 1;
    }

    private boolean checkSurroundings(String word, int x, int y, int xMultiplier, int yMultiplier) {
        //checks to see if the space that precedes where we want to put the word is blank or not (blank is good)
        if (y - yMultiplier >= 0 && x - xMultiplier >= 0) {
            if (!(grid[y - yMultiplier][x - xMultiplier] == '-')) {
                return false;
            }
        }
        //checks to see if the space that is at the end of where we want to put the word is blank or not (blank is good)
        if (y + word.length() * yMultiplier < grid.length && x + word.length() * xMultiplier < grid.length) {
            if (!(grid[y + word.length() * yMultiplier][x + word.length() * xMultiplier] == '-'))
                return false;
        }

        return true;
    }

    public String getLengthLongestString(String[] array) {
        int maxLength = 0;
        String longestString = null;
        for (String s : array) {
            if (s.length() > maxLength) {
                maxLength = s.length();
                longestString = s;
            }
        }
        return longestString;
    }


    void addDashes() {

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = '-';
            }
        }
    }

    /**
     * 获取带有序号的白色方块
     *
     * @param index
     * @return
     * @author cyd
     */
    public PdfPCell getNumberCell(int index) {
        Paragraph numberAndWord = new Paragraph(index + ".", italicCellFont);
        numberAndWord.setAlignment(Paragraph.ALIGN_LEFT);
        PdfPCell numberCell = new PdfPCell(numberAndWord);
        numberCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        numberCell.setBorderColor(BaseColor.GRAY);
        numberCell.setBackgroundColor(cellColor);
        numberCell.setMinimumHeight(CELL_HEIGHT);
        return numberCell;
    }


    /**
     * 获取线索的单元格
     * @param index
     * @return
     */
    public PdfPCell getCluesCell(int index,String clues) {
        Paragraph numberAndClues = new Paragraph(index + ". "+clues, cluesContentFont);
        numberAndClues.setLeading(20);
        numberAndClues.setAlignment(Paragraph.ALIGN_CENTER);
        PdfPCell cluesCell = new PdfPCell(numberAndClues);
//        cluesCell.setPaddingLeft(50);
        cluesCell.setPaddingBottom(15);
        cluesCell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        cluesCell.setBorderColor(BaseColor.WHITE);
        cluesCell.setMinimumHeight(CELL_HEIGHT);
        cluesCell.setPaddingLeft(30);
        return cluesCell;
    }

    //亚洲字体
    public static BaseFont getAsianFont() throws IOException , DocumentException{
        return BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
    }

    class WordPos {
        public String word;//单词
        public int x;//横坐标
        public int y;//纵坐标
        public int d;//方向 :0->横向  1->纵向
        public String clue;//线索

        //根据word判断唯一性
        @Override
        public String toString() {
            return "WordPos [word=" + word + "]";
        }
    }

    public Crossword() {
    }

}
