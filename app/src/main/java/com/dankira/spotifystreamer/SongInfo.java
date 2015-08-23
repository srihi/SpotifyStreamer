package com.dankira.spotifystreamer;

import java.io.Serializable;

/**
 * Created by da on 6/18/2015.
 */
public class SongInfo implements Serializable {
    private String albumArtUrl;
    private String songTitle;
    private String albumTitle;
    private String sampleStreamUrl;
    private String artistName;

    public String getAlbumArtUrl() {
        return albumArtUrl;
    }

    public void setAlbumArtUrl(String albumArtUrl) {
        this.albumArtUrl = albumArtUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getSampleStreamUrl() {
        return sampleStreamUrl;
    }

    public void setSampleStreamUrl(String sampleStreamUrl) {
        this.sampleStreamUrl = sampleStreamUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
