package com.chshru.game2048.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chshru.game2048.util.Config;

/**
 * Created by chshru on 2017/11/11.
 */


@SuppressLint("AppCompatCustomView")
public class Text extends TextView {
    public Text(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Text(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Text(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setTextColor(Config.FONT_BLACK);
        AssetManager am = context.getAssets();
        Typeface font = Typeface.createFromAsset(am, "fonts/my_font.ttf");
        setTypeface(font);
    }
}
