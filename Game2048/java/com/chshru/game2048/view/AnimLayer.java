package com.chshru.game2048.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.chshru.game2048.util.Config;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by chshru on 2017/11/8.
 */

public class AnimLayer extends FrameLayout {

    private Game mGame;
    private Handler mHandler;

    public AnimLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimLayer(Context context) {
        super(context);
    }


    public AnimLayer setGame(Game game) {
        mHandler = new Handler(game.getContext().getMainLooper());
        mGame = game;
        return this;
    }

    private Cell getCard(int num) {
        Cell c = new Cell(getContext());
        addView(c.setNum(num).fresh());
        return c;
    }

    /**
     * build animation for creating new num.
     */
    public void randomNumAnimation(Cell target) {
        ScaleAnimation sa = new ScaleAnimation(
                0.1f, 1f, 0.1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        sa.setDuration(Config.CREATE_TIME);
        target.setAnimation(null);
        target.getLabel().startAnimation(sa);
    }

    /**
     * build animation for moving cells.
     */
    public void cellMoveAnimation(final Cell from, final Cell to,
                                  int x1, int x, int y1, int y, final boolean bubble) {

        final Cell c = getCard(from.getNum());
        LayoutParams lp = new LayoutParams(Config.cellWidth, Config.cellWidth);
        lp.leftMargin = x1 * Config.cellWidth;
        lp.topMargin = y1 * Config.cellWidth;
        c.setLayoutParams(lp);
        TranslateAnimation ta = new TranslateAnimation(
                0, Config.cellWidth * (x - x1),
                0, Config.cellWidth * (y - y1)
        );
        ta.setDuration(Config.MOVE_TIME);
        ta.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                to.fresh();
                if (bubble) {
                    cellBubbleAnimation(to);
                    mGame.updateScore(to);
                }
                if (mGame.isMoved()) {
                    mGame.addRandomNum();
                    mGame.setMoved(false);
                }
                removeView(c);
            }
        });
        c.startAnimation(ta);
    }

    /**
     * build animation for merging cells.
     */
    private void cellBubbleAnimation(final Cell c) {
        ScaleAnimation sa = new ScaleAnimation(
                1f, 1.3f, 1f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        sa.setDuration(Config.MERGE_TIME);
        c.setAnimation(null);
        sa.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation sa1 = new ScaleAnimation(
                        1.3f, 1f, 1.3f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                sa1.setDuration(Config.MERGE_TIME);
                c.setAnimation(null);
                c.getLabel().startAnimation(sa1);
            }
        });
        c.getLabel().startAnimation(sa);
    }

    public void cellDisappearAnimation(final Cell c) {
        ScaleAnimation sa = new ScaleAnimation(
                1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        sa.setDuration(5 * Config.MERGE_TIME);
        sa.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                c.fresh();
                if (!mGame.hasReset()) {
                    mGame.setReset(true);
                    mGame.pop();
                }
            }
        });
        c.setAnimation(null);
        c.getLabel().startAnimation(sa);
    }

    public void cellAppearAnimation(final Cell c) {
        ScaleAnimation sa = new ScaleAnimation(
                0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        sa.setDuration(5 * Config.MERGE_TIME);
        sa.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                c.fresh();
            }
        });
        c.setAnimation(null);
        c.getLabel().startAnimation(sa);
    }


    /**
     * build animation for dialog while select column count.
     */
    public void shadowMoveAnimation(final Text from, final Text to) {
        final Text m = new Text(getContext());
        m.setBackgroundColor(Config.COLOR_DIVISION);
        int width = from.getRight() - from.getLeft();
        int x1 = Integer.valueOf(from.getText().toString());
        int x = Integer.valueOf(to.getText().toString());
        int height = from.getBottom() - from.getTop();
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.leftMargin = (int) ((x1 - Config.MIN_LINE + 0.5) * width);
        layoutParams.topMargin = 0;
        m.setLayoutParams(layoutParams);
        addView(m);
        TranslateAnimation ta = new TranslateAnimation(
                0, (x - x1) * width, 0, 0
        );
        ta.setDuration(Config.MOVE_TIME * 2);
        ta.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                from.setTextColor(Config.FONT_BLACK);
                from.setBackgroundColor(0);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                to.setBackgroundColor(Config.COLOR_DIVISION);
                to.setTextColor(Config.FONT_WHITE);
                m.setVisibility(View.INVISIBLE);
                m.setAnimation(null);
                removeView(m);
            }
        });
        m.startAnimation(ta);
    }

    public void titleAnimation(final View v) {
        final View parent = (View) v.getParent();
        int x = (v.getRight() - v.getLeft()) / 2;
        int y = (v.getBottom() - v.getTop()) / 2;
        RotateAnimation ra = new RotateAnimation(0, (20 * 360), x, y);
        ra.setDuration(3000);
        ScaleAnimation sa = new ScaleAnimation(
                1f, 0.5f, 1f, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        sa.setDuration(1500);
        sa.setAnimationListener(new AnimListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation sa = new ScaleAnimation(
                        0.5f, 1f, 0.5f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                sa.setDuration(1500);
                parent.startAnimation(sa);
            }
        });
        v.startAnimation(ra);
        parent.startAnimation(sa);
    }

    public void boardAnimation(final List<View> list) {
        final int[] flags = {0, 0};
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (flags[0] == list.size()) {
                    this.cancel();
                } else if (flags[1] == 0) {
                    final View v = list.get(flags[0]);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
                            aa.setDuration(Config.CREATE_TIME * 2);
                            aa.setAnimationListener(new AnimListener() {

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    v.setVisibility(View.VISIBLE);
                                    flags[0]++;
                                    flags[1] = 0;
                                }
                            });
                            v.startAnimation(aa);
                            flags[1] = 1;
                        }
                    });
                }
            }
        };
        timer.schedule(task, 0, 50);
    }
}
