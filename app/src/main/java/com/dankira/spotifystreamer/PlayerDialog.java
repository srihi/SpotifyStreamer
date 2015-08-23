package com.dankira.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dawit on 8/23/2015.
 */
public class PlayerDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    private ArrayList<SongInfo> mSongInfoList;
    private int mSongPosition = -1;
    private boolean isPlayerLoaded;
    private SongInfo selectedSong;
    private TextView player_artist_name;
    private TextView player_album_title;
    private TextView player_song_title;
    private ImageView player_album_cover;
    private ToggleButton player_button_play;
    private SeekBar player_seek_bar;
    private Button player_button_prev;
    private Button player_button_next;
    private TextView player_seek_time;
    private TextView player_total_time;

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, null);
        player_artist_name = (TextView) view.findViewById(R.id.player_artist_name);
        player_album_title = (TextView) view.findViewById(R.id.player_album_title);
        player_song_title = (TextView) view.findViewById(R.id.player_song_title);
        player_album_cover = (ImageView) view.findViewById(R.id.player_album_art);
        player_seek_bar = (SeekBar) view.findViewById(R.id.player_seek_bar);
        player_button_play = (ToggleButton) view.findViewById(R.id.player_button_play);

        player_seek_bar.setOnSeekBarChangeListener(this);
        player_seek_time = (TextView)view.findViewById(R.id.player_seek_time);
        player_total_time = (TextView)view.findViewById(R.id.player_total_time);

        player_button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {
                    start(selectedSong);
                } else {
                    pause();
                }
            }
        });
        player_button_next = (Button) view.findViewById(R.id.player_button_next);

        player_button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSongPosition >= mSongInfoList.size() - 1) {
                    v.setEnabled(false);
                    return;
                } else {
                    mSongPosition = mSongPosition + 1;
                    setSongToPlay(mSongInfoList.get(mSongPosition));
                    start(mSongInfoList.get(mSongPosition));
                }
            }
        });
        player_button_prev = (Button) view.findViewById(R.id.player_button_prev);

        player_button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSongPosition <= 0) {
                    v.setEnabled(false);
                    return;
                } else {
                    mSongPosition = mSongPosition - 1;
                    setSongToPlay(mSongInfoList.get(mSongPosition));
                    start(mSongInfoList.get(mSongPosition));
                }
            }
        });


        Bundle argument = getArguments();

        if (argument != null) {
            mSongInfoList = (ArrayList<SongInfo>) argument.getSerializable("Top10List");
            mSongPosition = argument.getInt("Position");
            selectedSong = mSongInfoList.get(mSongPosition);
            setSongToPlay(selectedSong);
            start(selectedSong);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                while (isPlayerLoaded) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = MainActivity.samplerService.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                    final int total = MainActivity.samplerService.getMusicDuration();
                    final String totalTime = getAsTime(total);
                    final String curTime = getAsTime(currentPosition);

                    player_seek_bar.setMax(total); //song duration
                    player_seek_bar.setProgress(currentPosition);  //for current song progress
                    player_seek_bar.setSecondaryProgress(MainActivity.samplerService.getBufferPosition());   // for buffer progress

                    if (getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.samplerService.isPlaying()) {
                                if (player_button_play != null && !player_button_play.isChecked()) {
                                    player_button_play.setChecked(true);
                                }
                            } else {
                                if (player_button_play != null && player_button_play.isChecked()) {
                                    player_button_play.setChecked(false);
                                }
                            }
                            player_seek_time.setText(curTime);
                            player_total_time.setText(totalTime);
                        }
                    });
                }
            }
        }).start();
        return view;
    }

    private String getAsTime(int total) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) total), TimeUnit.MILLISECONDS.toSeconds((long) total));
    }

    private void setSongToPlay(SongInfo songToPlay) {
        if(songToPlay != null) {
            player_artist_name.setText(songToPlay.getArtistName());
            player_album_title.setText(songToPlay.getAlbumTitle());
            player_song_title.setText(songToPlay.getSongTitle());
            Picasso.with(getActivity())
                    .load(songToPlay.getAlbumArtUrl())
                    .resize(350, 450)
                    .centerInside()
                    .placeholder(R.drawable.music_album_ph)
                    .error(R.drawable.music_album_ph)
                    .into(player_album_cover);
            isPlayerLoaded = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPlayerLoaded = false;
    }

    private void pause() {
        MainActivity.samplerService.PauseMusicPlay();
    }

    private void start(SongInfo songToPlay) {
        Log.v(LOG_TAG, "Play is clicked... with song: " + selectedSong.getSongTitle());
        MainActivity.samplerService.setSong(songToPlay);
        MainActivity.samplerService.PlayMusic();
        player_button_play.setChecked(true);
        if(mSongPosition >= mSongInfoList.size()-1) {
            player_button_next.setEnabled(false);
        }
        else {
            player_button_next.setEnabled(true);
        }
        if(mSongPosition <=0)
        {
            player_button_prev.setEnabled(false);
        }
        else{
            player_button_prev.setEnabled(true);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser == false) return;
        int newPosition = progress;
        MainActivity.samplerService.Scrobb(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        MainActivity.samplerService.PauseMusicPlay();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MainActivity.samplerService.PlayMusic();
    }
}
