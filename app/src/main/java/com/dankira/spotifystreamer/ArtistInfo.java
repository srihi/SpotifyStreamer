package com.dankira.spotifystreamer;

import java.io.Serializable;

/**
 * Created by Dawit on 6/16/2015.
 */
public class ArtistInfo implements Serializable {
    private String artistImageUrl;
    private String artistId;
    private String artistName;

    public String getArtistImageUrl(){
        return this.artistImageUrl;
    }

    public void setArtistImageUrl(String url){
        this.artistImageUrl = url;
    }

    public String getArtistId(){return this.artistId;}

    public void setArtistId(String song){
        this.artistId = song;
    }

    public String getArtistName(){
        return this.artistName;
    }

    public void setArtistName(String album){
        this.artistName = album;
    }
}
