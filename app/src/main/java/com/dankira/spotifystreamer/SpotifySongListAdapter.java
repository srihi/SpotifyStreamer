package com.dankira.spotifystreamer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dawit on 6/16/2015.
 */
public class SpotifySongListAdapter extends ArrayAdapter<SongInfo> {

    static class ViewHolder {
        private ImageView albumArtImageView;
        private TextView albumTitleTextView;
    }

    private int layout;
    private ArrayList<SongInfo> songInfoArrayList;
    //private DrawableCache drawableCache = DrawableCache.getInstance();

    //implementation example here http://stackoverflow.com/questions/6472248/how-use-arrayadaptert-with-complex-layout
    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        if (contentView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);

            if (view != null) {
                viewHolder = new ViewHolder();

                viewHolder.albumArtImageView = (ImageView) view.findViewById(R.id.search_result_imageView);
                viewHolder.albumTitleTextView = (TextView) view.findViewById(R.id.search_result_artist);
                view.setTag(viewHolder);
            }
        }
        else
        {
            view = contentView;
            viewHolder = (ViewHolder) contentView.getTag();
        }

        if (viewHolder != null) {
            SongInfo songInfo = songInfoArrayList.get(position);
            if (songInfo != null) {
                Picasso.with(getContext())
                        .load(songInfo.getAlbumArtUrl())
                        .resize(150, 150)
                        .centerCrop()
                        .placeholder(R.drawable.music_album_ph)
                        .error(R.drawable.music_album_ph)
                        .into(viewHolder.albumArtImageView);
                viewHolder.albumTitleTextView.setText(songInfo.getAlbumTitle());
            }
        }


        return view;
    }

    public SpotifySongListAdapter(Context context, int layout, ArrayList<SongInfo> songInfoList) {
        super(context, layout, songInfoList);

        this.songInfoArrayList = songInfoList;
        this.layout = layout;
    }
}
