package com.dankira.spotifystreamer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import com.dankira.spotifystreamer.SongInfo;

import java.io.IOException;

/**
 * Created by Dawit on 8/23/2015.
 */
public class SpotifySamplerService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    public static final String ACTION_PLAY = "Play";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_PAUSE = "Pause";
    private MediaPlayer mMediaPlayer = null;
    private SongInfo mSongInfo;
    private MediaPlayerStatus mStatus;
    private int bufferPosition;
    private final IBinder serviceBinder = new MusicPlayerServiceBinder();
    public static SpotifySamplerService mSpotifySamplerServiceInstance = null;

    private final String LOG_TAG=this.getClass().getSimpleName();

    public int getBufferPosition() {
        return bufferPosition;
    }

    public void setBufferPosition(int bufferPosition) {
        this.bufferPosition = bufferPosition;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mSongInfo = null;
        mStatus = MediaPlayerStatus.IsRetrieving;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mStatus = MediaPlayerStatus.IsRetrieving;
        return false;
    }

    @Override
    public void onDestroy() {
        if(mMediaPlayer != null)
        {
            mMediaPlayer.release();
        }
        mStatus = MediaPlayerStatus.IsRetrieving;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mStatus = MediaPlayerStatus.IsPlaying;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPosition(percent * getMusicDuration() / 100);
    }

    public void setSong(SongInfo songInfo) {
        Log.v(LOG_TAG, "Setting new song info to ..."+ songInfo.getSongTitle());
        StopMusicPlay();
        mSongInfo = songInfo;
    }

    public SongInfo getSongInfo(){
        return mSongInfo;
    }
    private void StopMusicPlay() {
        if(mStatus.equals(MediaPlayerStatus.IsPlaying)){
            Log.v(LOG_TAG,"Media Player stopped...");
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mStatus = MediaPlayerStatus.IsStopped;
        }
    }

    public void PauseMusicPlay()
    {
        if(mStatus == MediaPlayerStatus.IsPlaying && mMediaPlayer != null)
        {
            Log.v(LOG_TAG,"Media Player Paused...");
            mMediaPlayer.pause();
            mStatus = MediaPlayerStatus.IsPaused;
        }
    }

    public void PlayMusic()
    {
        if(mStatus.equals(MediaPlayerStatus.IsPaused)) {
            Log.v(LOG_TAG, "Media Player resumed...");
            mMediaPlayer.start();
            mStatus = MediaPlayerStatus.IsPlaying;
            return;
        }

        Log.v(LOG_TAG,"Media Player reset and restarted with new song info...");
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mSongInfo.getSampleStreamUrl());
        } catch (IOException e) {
            Log.v(LOG_TAG, "Exception occurred while setting data source url " + mSongInfo.getSampleStreamUrl() + " Exception occurred is " + e.getLocalizedMessage());
        }

        try{
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException exception){
            Log.v(LOG_TAG," Exception occurred while preparing the media url" + mSongInfo.getSampleStreamUrl() + " Exception occured is "+exception.getLocalizedMessage());
        }

        mStatus = MediaPlayerStatus.IsPreparing;
    }

    public boolean isPlaying()
    {
        return mStatus.equals(MediaPlayerStatus.IsPlaying);
    }

    public int getMusicDuration() {
        if(!mStatus.equals(MediaPlayerStatus.IsPreparing))
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    public int getCurrentPosition() {

        if (mStatus.equals(MediaPlayerStatus.IsPlaying) || mStatus.equals(MediaPlayerStatus.IsPaused))
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    public void Scrobb(int position)
    {
        if(!mStatus.equals(MediaPlayerStatus.IsPreparing) && !mStatus.equals(MediaPlayerStatus.IsRetrieving))
        {
            mMediaPlayer.seekTo(position);
        }
    }


    public enum MediaPlayerStatus{
        IsRetrieving,
        IsStopped,
        IsPreparing,
        IsPlaying,
        IsPaused
    }

    public class MusicPlayerServiceBinder extends Binder {
        public SpotifySamplerService getService() {
            return SpotifySamplerService.this;
        }
    }

}
