package com.saracraba.garageradio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @Created by Sara Craba.
 */
public class MainActivity extends Activity
{
    // constants
    private static final String URL_STREAMING= "http://192.240.102.5:7074/";
    private static final String URL_TITLE_NAME= "http://www.streamlicensing.com/directory/index.cgi?action=snippet&sid=2019";
    private static final String URL_CHATROOM= "http://chatroll.com/embed/chat/garage-radio?name=garage-radio";

    // layout objects
    private static Button mButtonPlay;
    private static TextView mTitleTextView;
    private static Drawable buttonPlay;

    // chat box
    private WebView mChatWebView;

    // media player manager
    private static MediaPlayerManager mediaPlayer;

    // thread to manage current title played
    private static DownloadTitle downloadTitle= new DownloadTitle();

    // notification manager
    private static com.saracraba.garageradio.NotificationManager notificationManager;

    protected static boolean refreshTitle=false;
    private static  MainActivity mainActivity;

    /**
     * Initialize layout objects for the user interface.
     * Allow the use of JavaScript in the chatbox.
     * Create a new private MediaPlayer to perform the streaming.
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

        buttonPlay=getResources().getDrawable(R.drawable.button_stop);

        mainActivity =this;

        // Enabling Java Script on chat box
        mChatWebView.getSettings().setJavaScriptEnabled(true);
        mChatWebView.loadUrl(URL_CHATROOM);

        // creating new media player
        mediaPlayer= new MediaPlayerManager(URL_STREAMING);

        //creating a notification manager
        notificationManager = new com.saracraba.garageradio.NotificationManager(mainActivity);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Checking internet connection
        if(!FirstPageActivity.networkManager.getStatus())
        {

            // Changing button image
            mButtonPlay.setBackground(getResources().getDrawable(R.drawable.button_play));

            // Making a toast to indicate there is not a internet connection
            Toast.makeText(getApplicationContext(),NetworkManager.ERROR_MESSAGE, Toast.LENGTH_LONG).show();

            return;
        }

        // start title refreshing if the media player is streaming
        if(mediaPlayer!=null)
        {
            if(mediaPlayer.getStatus()== mediaPlayer.IS_PLAYING)
            {
                startTitleRefreshing();
            }
        }
    }

    /**
     * Button Play/Stop.
     *
     * If the MediaPlayer is streaming, stop the streaming, update the button image and stops the thread to refresh the title,
     * otherwise starts the MediaPlayer, update the button image and start a new thread to refresh the title TextView
     */
    public void onPlayButtonClicked(View v) {
        // Checking internet connection
        if(!FirstPageActivity.networkManager.getStatus())
        {
            // Making a toast to indicate there is not a internet connection
            Toast.makeText(getApplicationContext(), NetworkManager.ERROR_MESSAGE, Toast.LENGTH_LONG).show();

            return;
        }

        //starts/stop the streaming, title refreshing and notifications
        if(mediaPlayer.getStatus()== MediaPlayerManager.IS_STOPPED)
        {
            mTitleTextView.setText("Connessione in corso...");

            mButtonPlay.setClickable(false);

            // start streaming
            mediaPlayer.start();
        }

        else //Media player is streaming
        {
            // stop streaming
            mediaPlayer.stop();

            // Changing button image
            mButtonPlay.setBackground(getResources().getDrawable(R.drawable.button_play));

            // Cleaning title view
            mTitleTextView.setText("");

            notificationManager.stop();

            stopTitleRefreshing();
        }
    }

    /**
     * This method is called when the media player start the streaming.
     * Change button image, fire the notification and start the thread to refresh the title
     */
    protected final static void notifyWhenReady()
    {
        // Changing button image
        mButtonPlay.setBackground(buttonPlay);
        mButtonPlay.setClickable(true);

        mTitleTextView.setText("Caricamento artista e titolo...");

        notificationManager.start();

        startTitleRefreshing();
    }

    /**
     * Button Quit.
     *
     * Stop the MediaPlayer and close the application
     */
    public void onQuitButtonClicked(View v)
    {
        stopTitleRefreshing();

        notificationManager.stop();

        //destroy the MediPlayer
        mediaPlayer.onDestroy();

        // Close the application
        finish();
    }


    /**
     * Start the thread to refresh the title each SECOND_TITLE_REFRESH seconds
     */
    private static  void startTitleRefreshing()
    {
        refreshTitle=true;

        // Creating new DownloadTitle thread
        downloadTitle= new DownloadTitle();

        // Executing the new DownloadTitle thread
        downloadTitle.execute(URL_TITLE_NAME);
    }

    /**
     * Stop the thread that refresh the title
     */
    private void stopTitleRefreshing()
    {
        refreshTitle=false;

        if(downloadTitle!= null) {
            // Cancelling DownloadTitle thread
            downloadTitle.cancel(true);
            downloadTitle = null;
        }
    }


    /**
     * Update the title in the user interface
     * @param title new title to be displayed
     */
    public static  final  void refreshTitleView(String title)
    {
        mTitleTextView.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_palimpsest:
                Uri uri = Uri.parse("http://www.garageradio.it/palinsesto.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            case R.id.action_credits:
                new AlertDialog.Builder(this)
                        .setTitle("Crediti")
                        .setMessage("Questa applicazione e' stata realizzata da Sara Craba.")
                        .setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setNegativeButton(R.string.dialog_website, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("http://saracraba.com");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     *  Release the MediaPlayer so that resources used by the internal player engine associated with the MediaPlayer object can be released immediately
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (notificationManager!= null)
        {
            notificationManager.stop();
            notificationManager= null;
        }

        if(downloadTitle!= null) {
            // Cancelling DownloadTitle thread
            downloadTitle.cancel(true);
            downloadTitle = null;
        }

        if (mediaPlayer != null)
        {
            mediaPlayer.onDestroy();
        }

        //END STATE
        //mMediaPlayer is in 'End()' state: , it can no longer be used and there is no way to bring it back to any other state.
    }

}
