package com.dankira.spotifystreamer;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dankira.spotifystreamer.service.SpotifySamplerService;

public class MainActivity extends AppCompatActivity {

    Fragment mContent;
    public static boolean mTwoPane;
    public static final String DETAILFRAGMENT_TAG = "DETAILS_FRAGMENT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top10fragment_container) != null)
        {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-land). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            if (savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top10fragment_container, new ArtistTop10Fragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
        }

    }
    public static SpotifySamplerService samplerService;
    private boolean isServiceBound;
    private Intent serviceIntent;

    @Override
    public void onStart() {
        super.onStart();
        if(serviceIntent == null) {
            serviceIntent = new Intent(this, SpotifySamplerService.class);
            serviceIntent.setAction(SpotifySamplerService.ACTION_PLAY);
            bindService(serviceIntent, playerServiceConnection, BIND_AUTO_CREATE);
            startService(serviceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        samplerService = null;
        super.onDestroy();
    }

    //connect to the service
    private ServiceConnection playerServiceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SpotifySamplerService.MusicPlayerServiceBinder binder = (SpotifySamplerService.MusicPlayerServiceBinder)service;
            //get service
            samplerService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
