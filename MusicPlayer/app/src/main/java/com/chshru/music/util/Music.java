package com.chshru.music.util;

/**
 * Created by chshru on 2017/2/17.
 */
public class Music {
    private String name;
    private String path;
    private String artist;

    Music(String name, String path, String artist) {
        setName(name);
        setPath(path);
        setArtist(artist);
    }

    String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    String getPath() {
        return path;
    }

    private void setArtist(String artist) {
        this.artist = artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setPath(String path) {
        this.path = path;
    }
}
