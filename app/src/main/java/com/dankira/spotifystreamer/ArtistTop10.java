package com.dankira.spotifystreamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ArtistTop10 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_top10);

        if(savedInstanceState == null)
        {
            ArtistInfo passedArtistInfo = (ArtistInfo)getIntent().getSerializableExtra(MainActivityFragment.BUNDLE_TAG_SELECTED_ARTIST);
            Bundle args = new Bundle();
            args.putSerializable(MainActivityFragment.BUNDLE_TAG_SELECTED_ARTIST, passedArtistInfo);
            ArtistTop10Fragment df = new ArtistTop10Fragment();
            df.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.artist_top10_fragment_container, df, MainActivity.DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_top10, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
