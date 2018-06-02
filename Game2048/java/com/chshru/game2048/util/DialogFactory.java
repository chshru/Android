package com.chshru.game2048.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chshru.game2048.R;
import com.chshru.game2048.view.AnimLayer;
import com.chshru.game2048.view.Game;
import com.chshru.game2048.view.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chshru on 2017/11/9.
 */

public class DialogFactory {

    private Context mContext;
    private DataEditor mDataEditor;
    private Game mGame;
    private Handler mHandler;

    private DialogFactory(Context context) {
        mContext = context;
    }

    public DialogFactory setGame(Game game) {
        this.mGame = game;
        return this;
    }

    public DialogFactory setDataEditor(DataEditor dataEditor) {
        this.mDataEditor = dataEditor;
        return this;
    }

    public static DialogFactory getInstance(Context context) {
        return new DialogFactory(context);
    }

    /**
     * build a tips dialog.
     */
    public void makeTipDialog(String strTitle, String strContent, String strButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_tips, null);
        TextView title = (TextView) view.findViewById(R.id.tips_title);
        TextView content = (TextView) view.findViewById(R.id.tips_content);
        TextView cancel = (TextView) view.findViewById(R.id.tips_shutdown);
        content.setText(strContent);
        title.setText(strTitle);
        cancel.setText(strButton);
        content.setTextSize(Config.BUTTON_TEXT_SIZE);
        cancel.setTextSize(Config.BUTTON_TEXT_SIZE);
        title.setTextSize(Config.BUTTON_TEXT_SIZE);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(backListener);
        dialog.setCancelable(false);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * build a board dialog.
     */
    @SuppressLint("SetTextI18n")
    public void makeBoardDialog(int itemHeight, String strTitle, String strButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_board, null);
        Text title = (Text) view.findViewById(R.id.board_title);
        Text button = (Text) view.findViewById(R.id.board_button);
        LinearLayout content = (LinearLayout) view.findViewById(R.id.board_content);
        view.findViewById(R.id.board_button_box).setVisibility(View.INVISIBLE);
        title.setText(strTitle);
        title.setTextSize(Config.BUTTON_TEXT_SIZE);
        button.setText(strButton);
        button.setTextSize(Config.BUTTON_TEXT_SIZE);
        final List<View> list = new ArrayList<>();
        for (int i = Config.MIN_LINE; i <= Config.MAX_LINE; i++) {
            LinearLayout temp = new LinearLayout(mContext);
            temp.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams lp1 = new LayoutParams(-1, itemHeight);
            content.addView(temp, lp1);
            temp.setVisibility(View.INVISIBLE);
            list.add(temp);
            Text mv1 = new Text(mContext);
            mv1.setGravity(Gravity.CENTER);
            mv1.setText(String.valueOf(i) + "•" + String.valueOf(i));
            mv1.setTextSize(Config.TITLE_TEXT_SIZE / 2);
            LayoutParams lp2 = new LayoutParams(0, -1);
            lp2.weight = 1f;
            temp.addView(mv1, lp2);
            mv1 = new Text(mContext);
            mv1.setGravity(Gravity.CENTER);
            mv1.setText(String.valueOf(Config.scores[i]));
            mv1.setTextSize(Config.BUTTON_TEXT_SIZE);
            lp2 = new LinearLayout.LayoutParams(0, -1);
            lp2.weight = 1f;
            temp.addView(mv1, lp2);
        }
        list.add(view.findViewById(R.id.board_button_box));
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(backListener);
        dialog.setCancelable(false);
        dialog.show();
        mGame.getAnimLayer().boardAnimation(list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * build a dialog, this dialog can be used to select column count.
     */
    public void makeSelectDialog(String strTitle) {
        final View view = View.inflate(mContext, R.layout.dialog_select, null);
        TextView title = (TextView) view.findViewById(R.id.select_title);
        TextView save = (TextView) view.findViewById(R.id.select_save);
        TextView cancel = (TextView) view.findViewById(R.id.select_cancel);
        title.setText(strTitle);
        title.setTextSize(Config.BUTTON_TEXT_SIZE);
        save.setTextSize(Config.BUTTON_TEXT_SIZE);
        cancel.setTextSize(Config.BUTTON_TEXT_SIZE);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.select_father);
        final ArrayList<Text> list = new ArrayList<>();
        for (int i = Config.MIN_LINE - 1; i <= Config.MAX_LINE + 1; i++) {
            Text mv = new Text(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -1);
            mv.setTextSize(Config.BUTTON_TEXT_SIZE);
            mv.setGravity(Gravity.CENTER);
            mv.setTextColor(Config.FONT_BLACK);
            if (i == Config.line) {
                mv.setTextColor(Config.FONT_WHITE);
                mv.setBackgroundColor(Config.COLOR_DIVISION);
            }
            mv.setId(i);
            if (i >= Config.MIN_LINE && i <= Config.MAX_LINE) {
                lp.weight = 0.1f;
                mv.setText(String.valueOf(i));
            } else lp.weight = 0.05f;
            mv.setLayoutParams(lp);
            ll.addView(mv);
            if (i >= Config.MIN_LINE && i <= Config.MAX_LINE) {
                list.add(mv);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setOnKeyListener(backListener);
        dialog.setCancelable(false);
        dialog.show();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_cancel:
                        Config.line = mDataEditor.getColumnCount();
                        dialog.dismiss();
                        break;
                    case R.id.select_save:
                        if (mDataEditor.getColumnCount() != Config.line) {
                            mDataEditor.saveColumnCount();
                            mGame.freshView();
                        }
                        dialog.dismiss();
                        break;
                    default:
                        int id = v.getId();
                        for (Text m : list) {
                            if (m.getId() == id) {
                                AnimLayer animLayer = (AnimLayer) view.findViewById(R.id.select_animlayer);
                                animLayer.shadowMoveAnimation(list.get(Config.line - Config.MIN_LINE), m);
                                Config.line = Integer.valueOf(m.getText().toString());
                            }
                        }
                        break;
                }
            }
        };
        cancel.setOnClickListener(listener);
        save.setOnClickListener(listener);
        for (Text m : list) {
            m.setOnClickListener(listener);
        }
    }

    private DialogInterface.OnKeyListener backListener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
        }
    };

}
