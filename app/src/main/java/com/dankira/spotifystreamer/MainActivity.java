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

    public static boolean mTwoPane;
    public static final String DETAIL_FRAGMENT_TAG = "DETAILS_FRAGMENT";
    public static SpotifySamplerService samplerService;
    private boolean isServiceBound;
    private Intent serviceIntent;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.top10fragment_container) != null)
        {
            mTwoPane = true;
            if (savedInstanceState == null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top10fragment_container, new ArtistTop10Fragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
        }
        if(serviceIntent == null) {
            serviceIntent = new Intent(getApplicationContext(), SpotifySamplerService.class);
            bindService(serviceIntent, playerServiceConnection, BIND_AUTO_CREATE);
            startService(serviceIntent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        samplerService = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
