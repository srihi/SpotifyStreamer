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
 * Created by Dawit on 6/16/2015.
 */
public class SpotifyArtistListAdapter extends ArrayAdapter<ArtistInfo> {

    static class ArtistListViewHolder {
        private ImageView albumArtImageView;
        private TextView artistNameTextView;
    }

    private int layout;
    private ArrayList<ArtistInfo> artistInfoArrayList;
    //private DrawableCache drawableCache = DrawableCache.getInstance();

    //implementation example here http://stackoverflow.com/questions/6472248/how-use-arrayadaptert-with-complex-layout
    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View view = null;
        ArtistListViewHolder artistListViewHolder = null;

        if (contentView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);

            if (view != null) {
                artistListViewHolder = new ArtistListViewHolder();

                artistListViewHolder.albumArtImageView = (ImageView) view.findViewById(R.id.search_result_imageView);
                artistListViewHolder.artistNameTextView = (TextView) view.findViewById(R.id.search_result_artist);
                view.setTag(artistListViewHolder);
            }
        }
        else
        {
            view = contentView;
            artistListViewHolder = (ArtistListViewHolder) contentView.getTag();
        }

        if (artistListViewHolder != null) {
            ArtistInfo artistInfo = artistInfoArrayList.get(position);
            if (artistInfo != null) {
                Picasso.with(getContext())
                        .load(artistInfo.getArtistImageUrl())
                        .resize(150, 150)
                        .centerCrop()
                        .placeholder(R.drawable.music_album_ph)
                        .error(R.drawable.music_album_ph)
                        .into(artistListViewHolder.albumArtImageView);
                artistListViewHolder.artistNameTextView.setText(artistInfo.getArtistName());
            }
        }


        return view;
    }

    public SpotifyArtistListAdapter(Context context, int layout, ArrayList<ArtistInfo> artistInfoList) {
        super(context, layout, artistInfoList);

        this.artistInfoArrayList = artistInfoList;
        this.layout = layout;
        this.notifyDataSetChanged();
    }
}
