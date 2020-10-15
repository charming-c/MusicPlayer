package com.example.musicplayer.Bean;

import android.graphics.Bitmap;

public class MusicBean {
    //歌名
    public String title;
    //歌唱者
    public String artist;
    //专辑名
    public  String album;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getAlbumBip() {
        return albumBip;
    }

    public void setAlbumBip(int albumBip) {
        this.albumBip = albumBip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public  int length;
    //专辑图片
    public int albumBip;
    public String path;
    public boolean isPlaying;

}
