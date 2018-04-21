package com.chshru.music.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static android.provider.MediaStore.Audio.Media;

/**
 * Created by chshru on 2017/2/17.
 */
public class MusicList {

    private Context mContext;
    private ArrayList<Music> lists;

    private MusicList(Context context) {
        lists = new ArrayList<>();
        mContext = context;
        createMusicList();
    }

    public static MusicList getInstance(Context context) {
        return new MusicList(context);
    }

    public ArrayList<Music> getList() {
        return lists;
    }

    private void createMusicList() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );
        String name, path, artist;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(Media.TITLE));
                artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
                path = cursor.getString(cursor.getColumnIndex(Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndex(Media.DURATION));
                int isMusic = cursor.getInt(cursor.getColumnIndex(Media.IS_MUSIC));
                if (isMusic != 0 && duration > 60000)
                    lists.add(new Music(name, path, artist));
            }
        }
        Collections.sort(lists, cmp);
        if (cursor != null) {
            cursor.close();
        }
    }

    private Comparator<Music> cmp = new Comparator<Music>() {
        public int compare(Music o1, Music o2) {
            String str1 = o1.getName();
            String str2 = o2.getName();
            if (isEnglish(str1) && isEnglish(str2))
                return str1.compareTo(str2);
            else if (!isEnglish(str1) && !isEnglish(str2)) {
                String[] str = {str1, str2};
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                Arrays.sort(str, com);
                if (o1.getName().equals(str[0]))
                    return -1;
                return 1;
            } else {
                if (!isEnglish(str1))
                    str1 = getHeadChar(str1);
                if (!isEnglish(str2))
                    str2 = getHeadChar(str2);
                return str1.compareTo(str2);
            }
        }
    };

    private boolean isEnglish(String str) {
        char[] a = str.toCharArray();
        return (a[0] >= 'a' && a[0] <= 'z') || (a[0] >= 'A' && a[0] <= 'Z');
    }

    private String getHeadChar(String str) {
        return new ChinaInitial().getPyHeadStr(str, true);
    }


}
