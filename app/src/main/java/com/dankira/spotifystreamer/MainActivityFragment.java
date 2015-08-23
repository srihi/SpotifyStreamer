package com.dankira.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    private SpotifyArtistListAdapter searchResultListAdapter;
    private ArrayList<ArtistInfo> searchResultArray = new ArrayList<>();
    private String searchTerm = "Jay Z";
    private Toast toast;
    private final String BUNDLE_TAG_SEARCH_RESULT_ARRAY = "SEARCHRESULTARRAY";
    private String LOG_TAG = this.getClass().getSimpleName();
    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(searchResultArray != null && searchResultArray.size() !=0)
        {
            outState.putSerializable(BUNDLE_TAG_SEARCH_RESULT_ARRAY, searchResultArray);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null)
        {
            searchResultArray.clear();
            if(savedInstanceState.getSerializable(BUNDLE_TAG_SEARCH_RESULT_ARRAY) != null){
                ArrayList<ArtistInfo> savedResultArray = (ArrayList<ArtistInfo>)savedInstanceState.getSerializable(BUNDLE_TAG_SEARCH_RESULT_ARRAY);
                searchResultArray.addAll(savedResultArray);
                searchResultListAdapter.notifyDataSetChanged();
            }

        }
    }

    private void GetSearchResults() {
        if(!isNetworkAvailable())
        {
            ShowCustomToast(getString(R.string.connection_unavailable));
            return;
        }
        SpotifySearchTask task = new SpotifySearchTask();
        try
        {
            if(searchTerm == null || searchTerm.isEmpty())
            {
                ShowCustomToast(getString(R.string.search_term_empty));
                Log.v(LOG_TAG," An empty string or a null parameter was passed to this method.");
                return;
            }
            task.execute(searchTerm).get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            Log.v(LOG_TAG,e.getMessage());
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
            Log.v(LOG_TAG, e.getMessage());
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        searchResultListAdapter = new SpotifyArtistListAdapter(getActivity(),R.layout.list_item_artist,searchResultArray);

        ListView searchResultView = (ListView) fragmentView.findViewById(R.id.search_result_list);
        searchResultView.setAdapter(searchResultListAdapter);

        searchResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistInfo artistInfo = searchResultListAdapter.getItem(position);

                if (MainActivity.mTwoPane) {
                    Log.v(LOG_TAG,"Orientation is landscape....");
                    Bundle args = new Bundle();
                    args.putSerializable("SONGINFO", artistInfo);
                    ArtistTop10Fragment df = new ArtistTop10Fragment();
                    df.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.top10fragment_container, df, MainActivity.DETAILFRAGMENT_TAG)
                            .commit();
                } else {
                    Intent artistTop10DisplayIntent = new Intent(getActivity(), ArtistTop10.class);
                    artistTop10DisplayIntent.putExtra("SONGINFO", artistInfo);
                    startActivity(artistTop10DisplayIntent);
                }
            }
        });

        final EditText searchText = (EditText) fragmentView.findViewById(R.id.search_term_editText);
        if(searchText != null) {
            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    searchText.clearFocus();
                    searchTerm = searchText.getText().toString();
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        GetSearchResults();
                        InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        handled = true;
                    }
                    return handled;
                }
            });
        }
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void ShowCustomToast(String textMessage) {

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity().getApplicationContext(), textMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();

    }

    public class SpotifySearchTask extends AsyncTask<String,Void,Void> {

        private String LOG_TAG = this.getClass().getSimpleName();
        @Override
        protected Void doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            if(params.length == 0 || params[0] == null || params[0].isEmpty())
            {
                Log.v(LOG_TAG," An empty string or a null parameter was passed to this method.");
                return null;
            }

            try {
                ArtistsPager artistsPager = spotify.searchArtists(params[0]);
                int i = 0;

                if (artistsPager.artists.items.size() == 0) {

                    getActivity().runOnUiThread((new Runnable() {
                        @Override
                        public void run() {
                            ShowCustomToast(getString(R.string.no_artist_found));
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
                            Log.v(LOG_TAG, "No images found for ....." + s.name);
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
                        ShowCustomToast(error.getMessage());
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
