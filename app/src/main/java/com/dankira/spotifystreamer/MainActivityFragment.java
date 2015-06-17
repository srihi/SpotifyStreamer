package com.dankira.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    SpotifySongListAdapter searchResultListAdapter;
    ArrayList<SongInfo> searchResultArray;
    String searchTerm = "Jay Z";

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        searchResultArray.clear();
        GetSearchResults();
        searchResultListAdapter.notifyDataSetChanged();
    }

    private void GetSearchResults() {

        SpotifyApi api = new SpotifyApi();
        final SpotifyService spotify = api.getService();

        spotify.searchArtists(searchTerm, new Callback<ArtistsPager>() {

            @Override
            public void success(ArtistsPager artistsPager, Response response) {

                int i = 0;

                for (Artist s : artistsPager.artists.items)
                {
                    final SongInfo info = new SongInfo();
                    info.setAlbumTitle(s.name);
                    if (s.images.size() != 0)
                    {
                        info.setAlbumArtUrl(s.images.get(0).url);
                    }
                    else {
                        spotify.searchAlbums(s.name, new Callback<AlbumsPager>() {
                            @Override
                            public void success(AlbumsPager albumsPager, Response response)
                            {
                                if(albumsPager.albums.items.size() >0)
                                {
                                    info.setAlbumArtUrl(albumsPager.albums.items.get(0).images.get(0).url);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });

                    }
                    info.setAlbumTitle(s.name);
                    searchResultArray.add(i, info);
                    i++;
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        searchResultArray = new ArrayList<SongInfo>();
        searchResultListAdapter = new SpotifySongListAdapter(getActivity(),R.layout.list_item_song,searchResultArray);

        final ListView searchResultView = (ListView) fragmentView.findViewById(R.id.search_result_list);
        searchResultView.setAdapter(searchResultListAdapter);

        //final EditText searchText = (EditText) fragmentView.findViewById(R.id.search_term_editText);
//        if(searchText != null) {
//            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    searchTerm = searchText.getText().toString();
//
//                    boolean handled = false;
//
//                    if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
//                    {
//                        Log.v("SpotifyStreamer", "Search button pressed......" + actionId + "search term "+searchTerm);
//                        searchResultArray.clear();
//                        GetSearchResults();
//                        searchResultListAdapter.notifyDataSetChanged();
//                        searchResultView.invalidateViews ();
//                        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//
//                        handled = true;
//                    }
//                    return handled;
//                }
//            });
//        }
        return fragmentView;
    }
}
