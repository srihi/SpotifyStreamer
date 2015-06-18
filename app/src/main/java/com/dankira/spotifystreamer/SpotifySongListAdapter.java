package com.dankira.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by da on 6/18/2015.
 */
public class SpotifySongListAdapter extends ArrayAdapter<SongInfo> {

    public static class SongListViewHolder{
        private ImageView albumImageView;
        private TextView albumTitleTextView;
        private TextView songTitleTextView;
    }

    private int layout;
    private ArrayList<SongInfo> songInfoArrayList;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        SongListViewHolder songListViewHolder = null;

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout,null);

            if(view != null){
                songListViewHolder = new SongListViewHolder();
                songListViewHolder.albumImageView = (ImageView)view.findViewById(R.id.album_art_image);
                songListViewHolder.albumTitleTextView=(TextView)view.findViewById(R.id.song_album_text);
                songListViewHolder.songTitleTextView =(TextView)view.findViewById(R.id.song_title_text) ;
                view.setTag(songListViewHolder);
            }
        }
        else
        {
            view = convertView;
            songListViewHolder = (SongListViewHolder)convertView.getTag();
        }

        if(songListViewHolder != null)
        {
            SongInfo songInfo = songInfoArrayList.get(position);
            if(songInfo != null){
                Picasso.with(getContext())
                        .load(songInfo.getAlbumArtUrl())
                        .resize(200, 200)
                        .centerCrop()
                        .placeholder(R.drawable.music_album_ph)
                        .error(R.drawable.music_album_ph)
                        .into(songListViewHolder.albumImageView);
                songListViewHolder.albumTitleTextView.setText(songInfo.getAlbumTitle());
                songListViewHolder.songTitleTextView.setText(songInfo.getSongTitle());
            }
        }

        return view;
    }

    public SpotifySongListAdapter(Context context, int layout, ArrayList<SongInfo> objects) {
        super(context, layout, objects);
        this.layout = layout;
        this.songInfoArrayList = objects;
    }
}
