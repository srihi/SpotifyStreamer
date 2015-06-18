package com.dankira.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    SpotifyArtistListAdapter searchResultListAdapter;
    ArrayList<ArtistInfo> searchResultArray;
    String searchTerm = "Jay Z";
    Toast toast;

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("SEARCHRESULTARRAY",searchResultArray);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null)
        {
            searchResultArray = (ArrayList<ArtistInfo>)savedInstanceState.getSerializable("SEARCHRESULTARRAY");
            searchResultListAdapter.notifyDataSetChanged();
        }
    }

    //    @Override
//    public void onStart() {
//        super.onStart();
//        searchResultArray.clear();
//        GetSearchResults();
//        searchResultListAdapter.notifyDataSetChanged();
//    }

    private void GetSearchResults() {
        SpotifySearchTask task = new SpotifySearchTask();
        try {
            task.execute(searchTerm).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        searchResultArray = new ArrayList<ArtistInfo>();
        searchResultListAdapter = new SpotifyArtistListAdapter(getActivity(),R.layout.list_item_artist,searchResultArray);

        final ListView searchResultView = (ListView) fragmentView.findViewById(R.id.search_result_list);
        searchResultView.setAdapter(searchResultListAdapter);

        searchResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistInfo artistInfo = searchResultListAdapter.getItem(position);

                Intent artistTop10DisplayIntent = new Intent(getActivity(),ArtistTop10.class);
                artistTop10DisplayIntent.putExtra("SONGINFO", artistInfo);
                startActivity(artistTop10DisplayIntent);
            }
        });

        final EditText searchText = (EditText) fragmentView.findViewById(R.id.search_term_editText);
        if(searchText != null) {
            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    searchTerm = searchText.getText().toString();
                    boolean handled = false;

                    if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        Log.v("SpotifyStreamer", "Search button pressed......" + actionId + "search term "+searchTerm);
                        searchResultListAdapter.clear();
                        GetSearchResults();
                        searchResultListAdapter.notifyDataSetChanged();
                        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        handled = true;
                    }
                    return handled;
                }
            });
        }
        return fragmentView;
    }
    private void ShowCustomToast(String textMessage) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) getView().findViewById(R.id.toast_layout_root));

        TextView toastTextView = (TextView) toastLayout.findViewById(R.id.toast_textArea);
        toastTextView.setText(textMessage);

        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();

    }
    public class SpotifySearchTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();

            SpotifyService spotify = api.getService();
            try {
                ArtistsPager artistsPager = spotify.searchArtists(params[0]);
                int i = 0;

                if (artistsPager.artists.items.size() == 0) {

                    getActivity().runOnUiThread((new Runnable() {
                        @Override
                        public void run() {
                            ShowCustomToast("There are no artists maching search criteria.");
                        }
                    }));

                    return null;
                }

                for (Artist s : artistsPager.artists.items) {
                    final ArtistInfo info = new ArtistInfo();
                    info.setArtistName(s.name);
                    info.setArtistId(s.id);
                    if (s.images.size() != 0) {
                        info.setArtistImageUrl(s.images.get(0).url);
                    } else {
                        AlbumsPager albumsPager = spotify.searchAlbums(s.name);
                        if (albumsPager.albums.items.size() > 0) {
                            info.setArtistImageUrl(albumsPager.albums.items.get(0).images.get(0).url);
                        } else {
                            Log.v("SpotifyStreamer", "No images found for ....." + s.name);
                        }
                    }
                    info.setArtistName(s.name);
                    searchResultArray.add(i, info);
                    i++;
                }
            } catch (final RetrofitError error) {
                getActivity().runOnUiThread((new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            searchResultListAdapter.notifyDataSetChanged();
        }
    }
}
