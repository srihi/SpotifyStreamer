package com.dankira.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistTop10Fragment extends Fragment {

    SpotifySongListAdapter top10SongListAdapter;
    ArrayList<SongInfo> top10ResultArray;

    public ArtistTop10Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_artist_top10, container, false);

        top10ResultArray = new ArrayList<SongInfo>();
        top10SongListAdapter = new SpotifySongListAdapter(getActivity(),R.layout.list_item_song,top10ResultArray);
        ListView top10ListView  = (ListView)fragmentView.findViewById(R.id.top10_result_list);

        top10ListView.setAdapter(top10SongListAdapter);

        Intent referrerIntent;
        referrerIntent = getActivity().getIntent();
        if(referrerIntent != null){
            ArtistInfo passedArtistInfo = (ArtistInfo)referrerIntent.getSerializableExtra("SONGINFO");
            if(passedArtistInfo != null) {
                getActivity().setTitle(passedArtistInfo.getArtistName() + " Top 10");

                GetTop10Tracks(passedArtistInfo.getArtistId());

            }
        }

        return fragmentView;
    }

    private void GetTop10Tracks(String artistId) {

        SpotifyTop10Task task = new SpotifyTop10Task();

        try {
            task.execute(artistId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class SpotifyTop10Task extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Map<String, Object> map = new HashMap<>();
            map.put("country", "US");
            Tracks tracks = spotify.getArtistTopTrack(params[0],map);

            int i = 0;

            for (Track s : tracks.tracks)
            {
                final SongInfo info = new SongInfo();
                info.setSongTitle(s.name);
                if (s.album.images.size() != 0) {
                    info.setAlbumArtUrl(s.album.images.get(0).url);
                }

                info.setSongTitle(s.name);
                info.setAlbumTitle(s.album.name);
                top10ResultArray.add(i, info);

                i++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            top10SongListAdapter.notifyDataSetChanged();
        }
    }
}
