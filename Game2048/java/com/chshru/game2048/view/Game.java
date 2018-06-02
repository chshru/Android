package com.chshru.game2048.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.GridLayout;

import com.chshru.game2048.util.Config;
import com.chshru.game2048.util.DataEditor;
import com.chshru.game2048.util.DialogFactory;
import com.chshru.game2048.Listener;

import java.util.ArrayList;

/**
 * Created by chshru on 2017/11/1.
 */

public class Game extends GridLayout {

    private boolean moved;
    private boolean win;
    private Callback mCallback;
    private AnimLayer mAnimLayer;
    private boolean reset;
    private DialogFactory mDialoger;

    private History mCache;

    private void initView() {
        initializeConfig();
        setColumnCount(Config.line);
        setBackgroundColor(Config.COLOR_DIVISION);
        addCells(Config.cellWidth, Config.cellWidth);
    }


    public Game setAnimLayer(AnimLayer animLayer) {
        mAnimLayer = animLayer;
        return this;
    }

    /**
     * fresh main view while column change.
     */
    public void freshView() {
        initializeConfig();
        removeAllViews();
        setColumnCount(Config.line);
        addCells(Config.cellWidth, Config.cellWidth);
        mCallback.initializeLines();
        mCallback.reStartGame();
    }

    /**
     * add or remove touchlistener for main view.
     */
    public void setTouchListener(boolean flag) {
        if (flag) {
            setOnTouchListener(new Listener(this).getGameListemer());
        } else {
            setOnTouchListener(null);
        }
    }

    /**
     * initialize parameters of config.
     */
    private void initializeConfig() {
        DisplayMetrics display = getResources().getDisplayMetrics();
        Config.dispWidth = Math.min(display.widthPixels, display.heightPixels);
        Config.line = DataEditor.getInstance(getContext(), mCallback).getColumnCount();
        Config.winNum = (int) Math.pow(2, 3 * Config.line - 1);
        Config.margin = Config.dispWidth / (10 * Config.line + 1);
        Config.cellWidth = (Config.dispWidth - 4 * Config.margin) / Config.line;
        Config.cellTextSize = (float) (4.0 / Config.line * Config.CELL_STAN_SIZE);
        Config.map = new Cell[Config.line][Config.line];
        Config.scores = new int[Config.MAX_LINE + 1];
        DataEditor.getInstance(getContext(), mCallback).getBestScores();
        mCache = new History(new int[Config.line][Config.line], 0, 0);
    }

    /**
     * start a new game
     */
    public void startGame() {
        for (int i = 0; i < Config.line; i++)
            for (int j = 0; j < Config.line; j++)
                Config.map[i][j].setNum(0).fresh();
        win = false;
        mCache.count = 0;
        addRandomNum();
        addRandomNum();
    }

    /**
     * add one number at random.
     */
    public void addRandomNum() {
        ArrayList<Point> list = new ArrayList<>();
        for (int i = 0; i < Config.line; i++)
            for (int j = 0; j < Config.line; j++)
                if (Config.map[i][j].getNum() == 0)
                    list.add(new Point(i, j));
        if (list.size() >= 1) {
            int index = (int) (Math.random() * list.size());
            Point p = list.get(index);
            Config.map[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4).fresh();
            mAnimLayer.randomNumAnimation(Config.map[p.x][p.y]);
            checkGameOver();
        }
    }


    /**
     * check the map to find if you win or lose.
     */
    private void checkGameOver() {
        boolean isFull = true;
        boolean cantMerge = true;
        for (int i = 0; i < Config.line; i++) {
            for (int j = 0; j < Config.line; j++) {
                if (Config.map[i][j].getNum() >= Config.winNum && !win) {
                    win = true;
                    mDialoger.makeTipDialog(
                            "You Win",
                            "You have reached " + String.valueOf(Config.winNum) + "!",
                            "Continue"
                    );
                }
                if (isFull && cantMerge) {
                    if (Config.map[i][j].getNum() == 0) {
                        isFull = false;
                    } else {
                        int[] dx = {0, 1, 0, -1};
                        int[] dy = {-1, 0, 1, 0};
                        for (int k = 0; k < 4; k++) {
                            int x = i + dx[k];
                            int y = j + dy[k];
                            if (x >= 0 && x < Config.line && y >= 0 && y < Config.line
                                    && Config.map[i][j].getNum() == Config.map[x][y].getNum()) {
                                cantMerge = false;
                            }
                        }
                    }
                }
            }
        }
        if (isFull && cantMerge) {
            mDialoger.makeTipDialog(
                    "Game Over",
                    "Cells are full and can't move!",
                    "Shutdown"
            );
        }
    }

    /**
     * add cells to map, map size from config.line.
     */
    private void addCells(int width, int height) {
        for (int i = 0; i < Config.line; i++) {
            for (int j = 0; j < Config.line; j++) {
                Config.map[i][j] = new Cell(getContext()).setNum(0).fresh();
                addView(Config.map[i][j], width, height);
            }
        }
    }

    /**
     * move cells while swipe, and merge the same cells.
     */
    private void move(int x, int y, int x1, int y1) {
        setMoved(true);
        Config.map[x][y].setNum(Config.map[x1][y1].getNum() + Config.map[x][y].getNum());
        Config.map[x1][y1].setNum(0).fresh();
    }

    private void createMoveAnim(Cell from, Cell to, int fx, int tx,
                                int fy, int ty, boolean bubble) {
        mAnimLayer.cellMoveAnimation(from, to, fx, tx, fy, ty, bubble);
    }

    /**
     * fresh map for each direction when swiping.
     */
    public void swipe(int direction) {
        setMoved(false);
        for (int i = 0; i < Config.line; i++) {
            for (int j = 0; j < Config.line; j++) {
                mCache.map[i][j] = Config.map[i][j].getNum();
            }
        }
        mCache.score = mCallback.getScore();
        mCache.best = Config.scores[Config.line];
        switch (direction) {
            case Listener.SWIPE_UP:
                for (int n = 0; n < Config.line; n++)
                    for (int i = 0; i < Config.line; i++)
                        for (int j = i + 1; j < Config.line; j++) {
                            int a = Config.map[i][n].getNum();
                            int b = Config.map[j][n].getNum();
                            if (b != 0 && (a == 0 || a == b)) {
                                createMoveAnim(getCell(j, n), getCell(i, n),
                                        n, n, j, i, a == b);
                                move(i, n, j, n);
                                if (a == 0) i--;
                            }
                            if (b != 0) break;
                        }
                break;
            case Listener.SWIPE_DOWN:
                for (int n = 0; n < Config.line; n++)
                    for (int i = Config.line - 1; i >= 0; i--)
                        for (int j = i - 1; j >= 0; j--) {
                            int a = Config.map[i][n].getNum();
                            int b = Config.map[j][n].getNum();
                            if (b != 0 && (a == 0 || a == b)) {
                                createMoveAnim(getCell(j, n), getCell(i, n),
                                        n, n, j, i, a == b);
                                move(i, n, j, n);
                                if (a == 0) i++;
                            }
                            if (b != 0) break;
                        }
                break;
            case Listener.SWIPE_LEFT:
                for (int n = 0; n < Config.line; n++)
                    for (int i = 0; i < Config.line; i++)
                        for (int j = i + 1; j < Config.line; j++) {
                            int a = Config.map[n][i].getNum();
                            int b = Config.map[n][j].getNum();
                            if (b != 0 && (a == 0 || a == b)) {
                                createMoveAnim(getCell(n, j), getCell(n, i),
                                        j, i, n, n, a == b);
                                move(n, i, n, j);
                                if (a == 0) i--;
                            }
                            if (b != 0) break;
                        }
                break;
            case Listener.SWIPE_RIGHT:
                for (int n = 0; n < Config.line; n++)
                    for (int i = Config.line - 1; i >= 0; i--)
                        for (int j = i - 1; j >= 0; j--) {
                            int a = Config.map[n][i].getNum();
                            int b = Config.map[n][j].getNum();
                            if (b != 0 && (a == 0 || a == b)) {
                                createMoveAnim(getCell(n, j), getCell(n, i),
                                        j, i, n, n, a == b);
                                move(n, i, n, j);
                                if (a == 0) i++;
                            }
                            if (b != 0) break;
                        }
                break;
        }
        if (moved) {
            mCache.count++;
        }
    }

    private Cell getCell(int x, int y) {
        return Config.map[x][y];
    }

    public void updateScore(Cell to) {
        mCallback.setScore(to.getNum() + mCallback.getScore());
    }


    public Game(Context context) {
        super(context);
        initView();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void doubleClick() {
        if (mCache.count < 5) {
            return;
        }
        mCache.count = 0;
        setReset(false);
        for (int i = 0; i < Config.line; i++) {
            for (int j = 0; j < Config.line; j++) {
                if (Config.map[i][j].getNum() != 0) {
                    Config.map[i][j].setNum(0);
                    mAnimLayer.cellDisappearAnimation(Config.map[i][j]);
                }
            }
        }
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean hasReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public void pop() {
        int[][] temp = mCache.map;
        for (int i = 0; i < Config.line; i++) {
            for (int j = 0; j < Config.line; j++) {
                if (temp[i][j] != 0) {
                    Config.map[i][j].setNum(temp[i][j]);
                    mAnimLayer.cellAppearAnimation(Config.map[i][j]);
                }
            }
        }
        mCallback.setScore(mCache.score);
        mCallback.setBest(mCache.best);
    }

    public void setmDialoger(DialogFactory dialoger) {
        this.mDialoger = dialoger;
    }

    public AnimLayer getAnimLayer() {
        return mAnimLayer;
    }


    public Game setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    public interface Callback {

        void initializeLines();

        void setScore(int score);

        void reStartGame();

        int getScore();

        void setWin(boolean win);

        void setBest(int best);
    }

    private class History {
        int[][] map;
        int score;
        int best;
        int count;

        History(int[][] map, int score, int best) {
            this.map = map;
            this.score = score;
            this.best = best;
        }
    }

}
