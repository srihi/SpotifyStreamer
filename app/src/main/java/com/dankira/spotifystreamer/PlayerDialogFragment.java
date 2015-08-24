package com.dankira.spotifystreamer;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
public class PlayerDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    private ArrayList<SongInfo> mSongInfoList;
    private int mSongPosition = -1;
    private boolean isPlaying = true;
    private SongInfo selectedSong;
    private TextView player_artist_name;
    private TextView player_album_title;
    private TextView player_song_title;
    private ImageView player_album_cover;
    private ImageButton player_button_play;
    private SeekBar player_seek_bar;
    private ImageButton player_button_prev;
    private ImageButton player_button_next;
    private TextView player_seek_time;
    private TextView player_total_time;

    private static final String BUNDLE_TAG_SONG_INFO = "SelectedSong";
    private static final String BUNDLE_TAG_TOP10_LIST="Top10SongsList";
    private static final String BUNDLE_TAG_WAS_PLAYING = "WasPlaying";
    private static final String BUNDLE_TAG_LAST_POSITION = "WasPlaying";
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
            selectedSong = (SongInfo)savedInstanceState.getSerializable(BUNDLE_TAG_SONG_INFO);
            isPlaying = (boolean)savedInstanceState.getBoolean(BUNDLE_TAG_WAS_PLAYING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, null);
        player_artist_name = (TextView) view.findViewById(R.id.player_artist_name);
        player_album_title = (TextView) view.findViewById(R.id.player_album_title);
        player_song_title = (TextView) view.findViewById(R.id.player_song_title);
        player_album_cover = (ImageView) view.findViewById(R.id.player_album_art);
        player_seek_bar = (SeekBar) view.findViewById(R.id.player_seek_bar);
        player_seek_bar.setOnSeekBarChangeListener(this);
        player_seek_time = (TextView)view.findViewById(R.id.player_seek_time);
        player_total_time = (TextView)view.findViewById(R.id.player_total_time);

        player_button_play = (ImageButton) view.findViewById(R.id.player_button_play);
        player_button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    play(selectedSong);
                } else {
                    pause();
                }
            }
        });

        player_button_next = (ImageButton) view.findViewById(R.id.player_button_next);
        player_button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSongPosition >= mSongInfoList.size() - 1) {
                    v.setEnabled(false);
                    return;
                } else {
                    mSongPosition = mSongPosition + 1;
                    selectedSong = mSongInfoList.get(mSongPosition);
                    setSongToPlay(selectedSong);
                    play(selectedSong);
                }
            }
        });

        player_button_prev = (ImageButton) view.findViewById(R.id.player_button_prev);
        player_button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSongPosition <= 0) {
                    v.setEnabled(false);
                    return;
                } else {
                    mSongPosition = mSongPosition - 1;
                    selectedSong = mSongInfoList.get(mSongPosition);
                    setSongToPlay(selectedSong);
                    play(selectedSong);
                }
            }
        });

        if(savedInstanceState != null)
        {
            mSongInfoList = (ArrayList<SongInfo>)savedInstanceState.getSerializable(BUNDLE_TAG_TOP10_LIST);
            selectedSong = (SongInfo)savedInstanceState.getSerializable(BUNDLE_TAG_SONG_INFO);
            mSongPosition = mSongInfoList.indexOf(selectedSong);
            setSongToPlay(selectedSong);
        } else {
            Bundle argument = getArguments();

            if (argument != null) {
                mSongInfoList = (ArrayList<SongInfo>) argument.getSerializable(ArtistTop10Fragment.BUNDLE_TAG_TOP10_LIST);
                mSongPosition = argument.getInt(ArtistTop10Fragment.BUNDLE_TAG_POSITION);
                selectedSong = mSongInfoList.get(mSongPosition);
                setSongToPlay(selectedSong);
                play(selectedSong);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }

                    if (getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (MainActivity.samplerService == null)
                                return;

                            int currentPosition = 0;
                            if (MainActivity.samplerService.isPlaying()) {
                                currentPosition = MainActivity.samplerService.getCurrentPosition();
                            }
                            else{
                                isPlaying = false;
                            }

                            final int total = MainActivity.samplerService.getMusicDuration();
                            final String totalTime = formatTime(total);
                            final String curTime = formatTime(currentPosition);

                            player_seek_bar.setMax(total);
                            player_seek_bar.setProgress(currentPosition);
                            player_seek_bar.setSecondaryProgress(MainActivity.samplerService.getBufferPosition());

                            if (MainActivity.samplerService.isPlaying()) {
                                if (player_button_play != null) {
                                    player_button_play.setImageResource(R.drawable.ic_pause_black_24dp);
                                }
                            } else {
                                if (player_button_play != null) {
                                    player_button_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_TAG_SONG_INFO, selectedSong);
        outState.putBoolean(BUNDLE_TAG_WAS_PLAYING, isPlaying);
        outState.putSerializable(BUNDLE_TAG_TOP10_LIST, mSongInfoList);
        if(MainActivity.samplerService.isPlaying())
            outState.putLong(BUNDLE_TAG_LAST_POSITION,MainActivity.samplerService.getCurrentPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        isPlaying = false;
    }

    private String formatTime(int total) {
        String formattedTime = "";

        if (total > 3600000)
            formattedTime = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours((long) total), TimeUnit.MILLISECONDS.toMinutes((long) total), TimeUnit.MILLISECONDS.toSeconds((long) total));
        else
            formattedTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) total), TimeUnit.MILLISECONDS.toSeconds((long) total));

        return formattedTime;
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
            isPlaying = true;
        }
    }

    private void pause() {
        MainActivity.samplerService.PauseMusic();
        isPlaying = false;
    }

    private void play(SongInfo songToPlay) {
        Log.d(LOG_TAG, "Play is clicked... with song: " + selectedSong.getSongTitle());
        MainActivity.samplerService.setSong(songToPlay);
        MainActivity.samplerService.PlayMusic();
        player_button_play.setImageResource(R.drawable.ic_pause_black_24dp);

        if(mSongPosition >= mSongInfoList.size()-1) {
            player_button_next.setEnabled(false);
            player_button_next.setClickable(false);
        }
        else {
            player_button_next.setEnabled(true);
            player_button_next.setClickable(true);
        }
        if(mSongPosition <=0)
        {
            player_button_prev.setEnabled(false);
            player_button_prev.setClickable(false);
        }
        else{
            player_button_prev.setEnabled(true);
            player_button_prev.setClickable(true);
        }
        isPlaying = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser == false) return;
        MainActivity.samplerService.Scrobb(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        MainActivity.samplerService.PauseMusic();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MainActivity.samplerService.PlayMusic();
    }
}
