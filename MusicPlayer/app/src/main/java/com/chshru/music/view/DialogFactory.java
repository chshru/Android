package com.chshru.music.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chshru.music.R;

/**
 * Created by chshru on 2017/5/17.
 */

public class DialogFactory {

    private Context context;

    private DialogFactory(Context context) {
        this.context = context;
    }

    public static DialogFactory getInstance(Context context) {
        return new DialogFactory(context);
    }

    public void tipsDialog(String strTitle, String strContent, String strButton) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_tips, null);
        TextView title = (TextView) view.findViewById(R.id.tips_title);
        TextView content = (TextView) view.findViewById(R.id.tips_content);
        Button cancel = (Button) view.findViewById(R.id.tips_shutdown);
        content.setText(strContent);
        title.setText(strTitle);
        cancel.setText(strButton);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
