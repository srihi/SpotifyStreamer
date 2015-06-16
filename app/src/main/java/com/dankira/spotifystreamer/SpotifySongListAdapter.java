package com.dankira.spotifystreamer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Dawit on 6/16/2015.
 */
public class SpotifySongListAdapter extends ArrayAdapter<SongInfo> {

    static class ViewHolder {
        private ImageView albumArtImageView;
        private TextView songTitleTextView;
        private TextView albumTitleTextView;
    }

    //implementation example here http://stackoverflow.com/questions/6472248/how-use-arrayadaptert-with-complex-layout
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public SpotifySongListAdapter(Context context, int resource, List<SongInfo> songInfoList) {
        super(context, resource, songInfoList);
    }
}
