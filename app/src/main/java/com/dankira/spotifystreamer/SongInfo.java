package com.dankira.spotifystreamer;

/**
 * Created by Dawit on 6/16/2015.
 */
public class SongInfo {
    private String albumArtUrl;
    private String songTitle;
    private String albumTitle;

    public String getAlbumArtUrl(){
        return this.albumArtUrl;
    }

    public void setAlbumArtUrl(String url){
        this.albumArtUrl = url;
    }

    public String getSongTitle(){
        return this.songTitle;
    }

    public void setSongTitle(String song){
        this.songTitle = song;
    }

    public String getAlbumTitle(){
        return this.albumTitle;
    }

    public void setAlbumTitle(String album){
        this.albumTitle = album;
    }
}
