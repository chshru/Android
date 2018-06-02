package com.chshru.game2048.util;

import com.chshru.game2048.view.Cell;

/**
 * Created by chshru on 2017/11/1.
 */

public class Config {


    public static int[] scores;
    public static int line;
    public static Cell[][] map;
    public static int margin;
    public static int dispWidth;
    public static int cellWidth;
    public static int winNum;
    public static float cellTextSize;


    /**
     * coefficient of boardsize and textsize.
     */
    public static final float CELL_STAN_SIZE = 25f * (6f / 7f);
    public static final float TITLE_TEXT_SIZE = 70f * (6f / 7f);
    public static final float SCORE_TEXT_SIZE = 15f * (6f / 7f);
    public static final float BUTTON_TEXT_SIZE = 20f * (6f / 7f);
    public static final float BOARD_SIZE_MOD = 0.5f * (6f / 7f);


    /**
     * duration of animation.
     */
    public static final int MERGE_TIME = 25;
    public static final int CREATE_TIME = 150;
    public static final int MOVE_TIME = 200;

    /**
     * range of column.
     */
    public static final int MIN_LINE = 3;
    public static final int MAX_LINE = 6;

    /**
     * file name.
     */
    static final String FILE_USER = "user";
    static final String COLUMN_NAME = "column";
    static final String FILE_MAP = "map";
    static final String BESTSCOR_ENAME = "score";

    /**
     * color of main view and font.
     */
    public static final int FONT_WHITE = 0xffffffff;
    public static final int FONT_BLACK = 0xff625e5a;
    public static final int COLOR_DIVISION = 0xffbbada0;

    /**
     * map of cells' color.
     */
    public static final int[] COLOR = {
            0xffd5cdc4, 0xffeee4da, 0xffede0c8, 0xfff2b179,
            0xfff59563, 0xfff67c5f, 0xfff65e3b, 0xffedcf72,
            0xffedcc61, 0xffedc850, 0xffedc53f, 0xffedc22e,
            0xff008573, 0xff00755e, 0xff483c32, 0xff483c32,
            0xff483c32, 0xff483c32, 0xff483c32, 0xff483c32,
            0xff483c32, 0xff483c32, 0xff483c32, 0xff483c32,
            0xff483c32, 0xff483c32, 0xff483c32, 0xff483c32,
            0xff483c32, 0xff483c32, 0xff483c32, 0xff483c32,
            0xff483c32, 0xff483c32, 0xff483c32, 0xff483c32,
            0xff483c32, 0xff483c32, 0xff483c32, 0xff483c32,
    };

}
