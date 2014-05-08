/**
 * Class MainActivity is the main Activity of Garage Radio application
 * In this activity user can play/stop the music and use the chat box
 * 
 * @author Sara Craba
 * @since 4/27/14
 * @version 2.0
 */
package com.garageradio.app.mainpage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.garageradio.app.R;
import com.garageradio.app.palimpsest.PalimpsestActivity;

import java.io.IOException;

/**
 * @see Activity on Android
 */
public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, AsyncResponse
{
    // constants
    private static final String MAIN_ACTIVITY= "MainActivity";
    private static final String URL_STREAMING= "http://192.240.102.5:7074/";
    private static final String URL_TITLE_NAME= "http://www.streamlicensing.com/directory/index.cgi?action=snippet&sid=2019";
    private static final String URL_CHATROOM= "http://chatroll.com/embed/chat/garage-radio?name=garage-radio";
    public static final String DOWNLOAD_TITLE= "DownloadTitle";
    public static final String PALIMPSEST_ACTIVITY= "PalimpsestActivity";
    public static final String DOWNLOAD_PALIMPSEST= "DownloadPalimpsest";
    public static final Integer SECOND_TITLE_REFRESH= 10;

    // layout objects
    private MediaPlayer mMediaPlayer;
    private Button mButtonPlay;
    private TextView mTitleTextView;

    // thread to manage current title played
    private DownloadTitle downloadTitle= new DownloadTitle();

    // chat box
    private WebView mChatWebView;

    // variables
    protected static boolean playing=false;
    protected static boolean refreshTitle=false;

    /**
     * @see onCreate() on Android Activity class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing layout objects
        mButtonPlay=(Button) findViewById(R.id.button_play);
        mTitleTextView= (TextView) findViewById(R.id.titleTextView);
        mChatWebView= (WebView) findViewById(R.id.chatView);

        // Enabling Java Script on chat box
        mChatWebView.getSettings().setJavaScriptEnabled(true);
        mChatWebView.loadUrl(URL_CHATROOM);

        // Configuring the media player
        configureMediaPlayer();
    }

    /**
     * @see onResume() on Android Activity class
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        // Checking internet connection
        if(!onNetworkConnection())
        {
            return;
        }

        // Starting title refresh thread if media player is playing
        if(playing)
        {
            startTitleRefreshing();
        }
    }

/*
 * buttons section
 */
    /**
     * Button play.
     *
     * If the media player is running stops it, otherwise starts streaming
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onPlayButtonClicked(View v)
    {
        // Checking internet connection
        if(!onNetworkConnection())
        {
            return;
        }

        if(!playing) //is stopped
        {
            playing= true;

            // Starting streaming
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();

            // Changing button image
            mButtonPlay.setBackground(getResources().getDrawable(R.drawable.button_stop));

            // Starting thread to refresh title
            startTitleRefreshing();
        }
        else //is playing
        {
            playing= false;

            // Stopping streaming
            mMediaPlayer.stop();

            // Changing button image
            mButtonPlay.setBackground(getResources().getDrawable(R.drawable.button_play));

            // Stopping thread to refresh title
            stopTitleRefreshing();

            // Cleaning title view
            mTitleTextView.setText("");
        }
    }

    /**
     * Button Palimpsest.
     *
     * Starts the Palimpsest activity
     */
    public void onPalimpsestButtonClicked(View v)
    {
        // Checking internet connection
        if(!onNetworkConnection())
        {
            return;
        }

        // Stopping title refreshing thread
        stopTitleRefreshing();

        // Starting Palimpsest activity
        Intent intent= new Intent(this, PalimpsestActivity.class);
        startActivity(intent);
    }

    /**
     * Button Copyright.
     *
     * Create a toast to show the copyright
     */
    public void onCopyrightButtonClicked(View v)
    {
        Toast.makeText(getApplicationContext(), "© 2014 Sara Craba", Toast.LENGTH_LONG).show();
    }

    /**
     * Button Quit.
     *
     * Stops the media player and close the application
     */
    public void onQuitButtonClicked(View v)
    {
        // Stopping refreshing title thread
        stopTitleRefreshing();

        playing= false;

        // Stopping streaming
        mMediaPlayer.stop();

        // Closing application
        finish();
    }

/*
 * button section end
 */

/*
 * private methods section
 */
    /**
     * Configure the media player
     *
     * 1. create new media player
     * 2. set the data source
     */
    private void configureMediaPlayer()
    {
        // Creating new media player
        mMediaPlayer = new MediaPlayer();

        // Registering a callback to be invoked when the status of a network stream's buffer has changed
        mMediaPlayer.setOnBufferingUpdateListener(this);

        // Registering a callback to be invoked when the end of a media source has been reached during playback.
        mMediaPlayer.setOnCompletionListener(this);

        try
        {
            // Setting the data source to use.
            mMediaPlayer.setDataSource(URL_STREAMING);
        }
        catch (IOException e)
        {
            Log.e(MAIN_ACTIVITY, e.toString());
        }
    }

    /**
     * Starts the thread to refresh the title each SECOND_TITLE_REFRESH seconds
     */
    private void startTitleRefreshing()
    {
        refreshTitle=true;

        // Creating new DownloadTitle thread
        downloadTitle= new DownloadTitle();
        downloadTitle.delegate = this;

        // Executing the new DownloadTitle thread
        downloadTitle.execute(URL_TITLE_NAME);
    }

    /**
     * Stops the thread that refresh the title
     */
    private void stopTitleRefreshing()
    {
        refreshTitle=false;

        // Cancelling DownloadTitle thread
        downloadTitle.cancel(true);
    }

/*
 * private methods section end
 */

/*
 * public methods section
 */
    /**
     * Refreshes the title text in the user interface
     */
    public void refreshTitleView(String title)
    {
        mTitleTextView.setText(title);
    }
/*
 * public methods section end
 */

/*
 *  HTTP connection section
 */
    /**
     * Checks for internet connection
     *
     * @return  true if a internet connection is available, false otherwise
     */
    private boolean onNetworkConnection()
    {
        // Getting System Service
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Check if there is internet connection
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }
        else
        {
            // Making a toast to indicate there is not a internet connection
            Toast.makeText(getApplicationContext(), "Connessione di rete non disponibile\n    abilita il wifi o i dati e riprova", Toast.LENGTH_LONG).show();
            return false;
        }
    }
/*
 * HTTP connection section end
 */

/*
 * override default functions section
 */
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) { }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) { }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        mediaPlayer.start();
    }
/*
 * override default functions section end
 */
}
