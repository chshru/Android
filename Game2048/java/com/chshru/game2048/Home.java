package com.chshru.game2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.chshru.game2048.util.Config;
import com.chshru.game2048.view.AnimLayer;
import com.chshru.game2048.view.Game;
import com.chshru.game2048.util.DataEditor;
import com.chshru.game2048.util.DialogFactory;

import java.util.ArrayList;

/**
 * Created by chshru on 2017/11/1.
 */

public class Home extends AppCompatActivity implements View.OnClickListener, Game.Callback {

    private Game mGame;
    private TextView scoreView;
    private TextView bestView;
    private TextView boardBtn;
    private TextView setBtn;
    private TextView resetBtn;
    private DataEditor mDataEditor;
    private DialogFactory mDialoger;

    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initialize();
    }

    private void initialize() {
        initializeLines();
        AnimLayer layer = (AnimLayer) findViewById(R.id.animlayer);
        mGame = (Game) findViewById(R.id.mainview);
        scoreView = (TextView) findViewById(R.id.score);
        scoreView.setTextSize(Config.SCORE_TEXT_SIZE);
        bestView = (TextView) findViewById(R.id.bestscore);
        bestView.setTextSize(Config.SCORE_TEXT_SIZE);
        TextView scoreText = (TextView) findViewById(R.id.scoretext);
        scoreText.setTextSize(Config.SCORE_TEXT_SIZE);
        TextView bestText = (TextView) findViewById(R.id.besttext);
        bestText.setTextSize(Config.SCORE_TEXT_SIZE);
        resetBtn = (TextView) findViewById(R.id.restart);
        resetBtn.setTextSize(Config.BUTTON_TEXT_SIZE);
        setBtn = (TextView) findViewById(R.id.set);
        setBtn.setTextSize(Config.BUTTON_TEXT_SIZE);
        boardBtn = (TextView) findViewById(R.id.board);
        boardBtn.setTextSize(Config.BUTTON_TEXT_SIZE);
        TextView title = (TextView) findViewById(R.id.title);
        title.setTextSize(Config.TITLE_TEXT_SIZE);
        title.setOnTouchListener(new Listener(mGame).getIntorListener());
        mDataEditor = DataEditor.getInstance(this, this);
        mDialoger = DialogFactory.getInstance(this);
        mDialoger.setDataEditor(mDataEditor).setGame(mGame);
        mGame.setCallback(this).setAnimLayer(layer).setmDialoger(mDialoger);
        layer.setGame(mGame);
        setTouchListener(true);
        setBest(mDataEditor.getBestScore());
        if (!mDataEditor.getTheLastMap()) {
            reStartGame();
        }
    }

    /**
     * initialize four divisions around mainbox.
     */
    public void initializeLines() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.mainviewbox);
        LayoutParams lp = (LayoutParams) ll.getLayoutParams();
        lp.height = Config.cellWidth * Config.line + Config.margin;
        ll.setLayoutParams(lp);

        TextView topLine = (TextView) findViewById(R.id.top_line);
        TextView bottomLine = (TextView) findViewById(R.id.bottom_line);
        TextView leftLine = (TextView) findViewById(R.id.left_line);
        TextView rightLine = (TextView) findViewById(R.id.right_line);
        //top division.
        LayoutParams layoutParams = (LayoutParams) topLine.getLayoutParams();
        layoutParams.width = Config.line * Config.cellWidth + Config.margin / 2 * 2;
        layoutParams.height = Config.margin / 2;
        topLine.setLayoutParams(layoutParams);
        //bottom division.
        layoutParams = (LayoutParams) bottomLine.getLayoutParams();
        layoutParams.height = Config.margin / 2;
        layoutParams.width = Config.cellWidth * Config.line + Config.margin / 2 * 2;
        bottomLine.setLayoutParams(layoutParams);
        //left division.
        layoutParams = (LayoutParams) leftLine.getLayoutParams();
        layoutParams.width = Config.margin / 2;
        layoutParams.height = Config.cellWidth * Config.line;
        leftLine.setLayoutParams(layoutParams);
        //right division.
        layoutParams = (LayoutParams) rightLine.getLayoutParams();
        layoutParams.width = Config.margin / 2;
        layoutParams.height = Config.cellWidth * Config.line;
        rightLine.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart:
                reStartGame();
                break;
            case R.id.set:
                mDialoger.makeSelectDialog("Select column");
                break;
            case R.id.board:
                DisplayMetrics display = getResources().getDisplayMetrics();
                int itemHeight = (int) (display.heightPixels * Config.BOARD_SIZE_MOD)
                        / (Config.MAX_LINE - Config.MIN_LINE + 1);
                mDialoger.makeBoardDialog(itemHeight, "Board", "Close");
                break;
        }
    }

    /**
     * set onTouchListener for all views.
     */
    public void setTouchListener(boolean flag) {
        if (flag) {
            setBtn.setOnClickListener(this);
            boardBtn.setOnClickListener(this);
            resetBtn.setOnClickListener(this);
        } else {
            setBtn.setOnClickListener(null);
            boardBtn.setOnClickListener(null);
            resetBtn.setOnClickListener(null);
        }
        mGame.setTouchListener(flag);
    }

    /**
     * start a new mGame.
     */
    public void reStartGame() {
        mGame.startGame();
        setBest(mDataEditor.getBestScore());
        setScore(0);
    }

    /**
     * set score num and fresh best score.
     */
    public void setScore(int score) {
        scoreView.setText(String.valueOf(score));
        this.score = score;
        if (score > getBest()) {
            setBest(score);
            mDataEditor.saveBestScore();
        }
    }

    public int getScore() {
        return score;
    }

    @Override
    public void setWin(boolean win) {
        mGame.setWin(win);
    }

    public void setBest(int score) {
        Config.scores[Config.line] = score;
        bestView.setText(String.valueOf(getBest()));
    }

    public int getBest() {
        return Config.scores[Config.line];
    }

    @Override
    protected void onPause() {
        mDataEditor.saveBestScore();
        mDataEditor.saveTheLastMap();
        mDataEditor.saveColumnCount();
        super.onPause();
    }
}
