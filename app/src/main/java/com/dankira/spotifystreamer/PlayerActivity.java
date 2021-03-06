package com.dankira.spotifystreamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);

        setContentView(R.layout.activity_player);

        if(savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            PlayerDialogFragment df = new PlayerDialogFragment();
            df.setArguments(extras);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.player_fragment_container, df, MainActivity.DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
