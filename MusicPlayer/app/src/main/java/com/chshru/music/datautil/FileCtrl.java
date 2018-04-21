package com.chshru.music.datautil;


import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by chshru on 2017/4/28.
 */

public class FileCtrl {

    private Context context;
    private String division = "___";

    private FileCtrl(Context context) {
        this.context = context;
    }

    public static FileCtrl getInstance(Context context) {
        return new FileCtrl(context);
    }

    public int getProcess() {
        return getFileData(Config.PROCESS, Config.FILE_PLAYINFO);
    }

    public int getPosition() {
        int result = getFileData(Config.POSITION, Config.FILE_PLAYINFO);
        if (result >= MusicList.getInstance(context).getList().size())
            result = MusicList.getInstance(context).getList().size() - 1;
        return result;
    }

    public void saveUserInfo() {
        try {
            FileOutputStream fOut = context.openFileOutput(Config.FILE_PLAYINFO, MODE_PRIVATE);
            fOut.write((Config.POSITION + division + Config.curPosition + "\r\n").getBytes());
            //fOut.write((Config.PROCESS + division + Player.getInstance(context).getCurTime() + "\r\n").getBytes());
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getFileData(String infoName, String fileName) {
        int result = 0;
        try {
            FileInputStream fin = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataS = line.split(division);
                if (dataS[0].equals(infoName)) {
                    result = Integer.valueOf(dataS[1]);
                    break;
                }
            }
            br.close();
            isr.close();
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
