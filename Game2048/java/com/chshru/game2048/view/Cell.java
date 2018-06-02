package com.chshru.game2048.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chshru.game2048.util.Config;


/**
 * Created by chshru on 2017/11/1.
 */

public class Cell extends FrameLayout {

    private int num;
    private Text label;

    private void initView() {
        Text back = new Text(getContext());
        back.setBackgroundColor(Config.COLOR[0]);
        label = new Text(getContext());
        label.setTextSize(Config.cellTextSize);
        label.setGravity(Gravity.CENTER);
        LayoutParams lp = new LayoutParams(-1, -1);
        int margin = Config.margin / 2;
        lp.setMargins(margin, margin, margin, margin);
        addView(label, lp);
        addView(back, lp);
        label.bringToFront();
    }

    public Cell fresh() {
        label.setText(num == 0 ? "" : num + "");
        int index = (int) (Math.log((double) num) / Math.log((double) 2));
        label.setBackgroundColor(num == 0 ? Config.COLOR[0] : Config.COLOR[index]);
        label.setTextColor(num >= 8 ? Config.FONT_WHITE : Config.FONT_BLACK);
        return this;
    }

    public Cell setNum(int num) {
        this.num = num;
        return this;
    }

    public TextView getLabel() {
        return label;
    }

    public int getNum() {
        return num;
    }

    public Cell(Context context) {
        super(context);
        initView();
    }

}
