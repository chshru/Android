package com.chshru.game2048.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.chshru.game2048.view.Game;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by chshru on 2017/11/9.
 */

public class DataEditor {

    private final String DIVISION = "___";

    private Context mContext;
    private Game.Callback mCallback;
    private SharedPreferences pref;


    private DataEditor(Context context, Game.Callback callback) {
        mContext = context;
        mCallback = callback;
        pref = mContext.getSharedPreferences(
                Config.FILE_USER, Context.MODE_PRIVATE);
    }

    /**
     * return an instance.
     */
    public static DataEditor getInstance(Context context, Game.Callback callback) {
        return new DataEditor(context, callback);
    }

    public int getBestScore() {
        return Config.scores[Config.line];
    }

    /**
     * return the column count, if its' null, return 4.
     */
    public int getColumnCount() {
        return pref.getInt(Config.COLUMN_NAME, 4);
    }

    /**
     * save column count to file.
     */
    public void saveColumnCount() {
        pref.edit().putInt(Config.COLUMN_NAME, Config.line).apply();
    }

    /**
     * initialize the map of best scores.
     */
    public void getBestScores() {
        for (int i = Config.MIN_LINE; i <= Config.MAX_LINE; i++) {
            String name = Config.BESTSCOR_ENAME + i;
            Config.scores[i] = pref.getInt(name, 0);
        }
    }

    /**
     * save best scores to file.
     */
    public void saveBestScore() {
        for (int i = Config.MIN_LINE; i <= Config.MAX_LINE; i++) {
            String name = Config.BESTSCOR_ENAME + i;
            pref.edit().putInt(name, Config.scores[i]).apply();
        }
    }

    /**
     * save map to file while you exit the game.
     */
    public void saveTheLastMap() {
        try {
            FileOutputStream fos = mContext.openFileOutput(Config.FILE_MAP, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            for (int i = 0; i < Config.line; i++) {
                for (int j = 0; j < Config.line; j++) {
                    bw.write(j == Config.line - 1 ? Config.map[i][j].getNum() + "\r\n"
                            : Config.map[i][j].getNum() + DIVISION);
                }
            }
            bw.write(mCallback.getScore() + "\r\n");
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get the map of last game, if its' returned fasle, start a new game.
     */
    public boolean getTheLastMap() {
        boolean flag = true;
        try {
            FileInputStream fin = mContext.openFileInput(Config.FILE_MAP);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);
            String[] line;
            for (int i = 0; i < Config.line; i++) {
                line = br.readLine().trim().split(DIVISION);
                if (line.length != Config.line) {
                    flag = false;
                    break;
                } else {
                    for (int j = 0; j < line.length; j++) {
                        Config.map[i][j].setNum(Integer.valueOf(line[j])).fresh();
                        if (Config.map[i][j].getNum() >= Config.winNum) {
                            mCallback.setWin(true);
                        }
                    }

                }
            }
            int score = Integer.valueOf(br.readLine());
            mCallback.setScore(score);
            br.close();
            isr.close();
            fin.close();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * get all num from file you afferented, if its' null, return 0.
     * the DIVISION at start of this file.
     */
    private int getInformationByName(String info, String file) {
        int result = 0;
        try {
            FileInputStream fin = mContext.openFileInput(file);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataS = line.split(DIVISION);
                if (dataS[0].equals(info)) {
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
