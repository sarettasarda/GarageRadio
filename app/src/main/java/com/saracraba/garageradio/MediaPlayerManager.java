package com.saracraba.garageradio;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Sara Craba
 *
 * Implements a MediaPlayer to stream from a URL.
 * The state diagram can be followed at: http://developer.android.com/reference/android/media/MediaPlayer.html
 */
public class MediaPlayerManager implements MediaPlayer.OnErrorListener,  MediaPlayer.OnPreparedListener
{
    public final static boolean IS_PLAYING= true;
    public final static boolean IS_STOPPED= false;

    private final String UrlStreaming;
    private static final String MEDIA_PLAYER= "Media Player";

    private static enum State{NULL, IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, END, ERROR};
    private static State currentState;

    private MediaPlayer mMediaPlayer= null;

    /**
     * Save Url to stream, put MediaPlayer in the initial NULL status.
     * MediaPlayer is not created yet.
     *
     * @param urlStreaming url to stream
     */
    public  MediaPlayerManager(final String urlStreaming)
    {
        if(urlStreaming==null)
        {
            throw new NullPointerException();
        }

        this.UrlStreaming= urlStreaming;

        currentState= State.NULL;

        createMediaPlayer();
    }

    /**
     * Create a MediaPlayer and put it in the IDLE state.
     * Register the callbacks for Errors and for the async Prepared state.
     */
    private final void createMediaPlayer()
    {
        // Creating new media player
        mMediaPlayer = new MediaPlayer();

        // changing media player state
        currentState= State.IDLE;

        // Register a callback to be invoked when a seek operation has been completed.
        mMediaPlayer.setOnPreparedListener(this);
        // Register a callback to be invoked when an error has happened during an asynchronous operation.
        mMediaPlayer.setOnErrorListener(this);

        configureMediaPlayer();
    }

    /**
     * Put the MediaPlayer in the IDLE state (if is not) and set the data source to use for stream
     */
    private final void configureMediaPlayer()
    {
        // initialize mMediaPlayer if it is null
        if(currentState== State.NULL)
        {
            createMediaPlayer();
        }

        // reset mMediaPlayer if is not in the IDLE state
        if(currentState!= State.IDLE)
        {
            mMediaPlayer.reset();
        }

        // mMediaPlayer in IDLE state

        try
        {
            // Setting the data source to use.
            mMediaPlayer.setDataSource(UrlStreaming);
        }
        catch (IOException e)
        {
            Log.e(MEDIA_PLAYER, "IOException: " + e.toString());

            errorMediaPlayer(mMediaPlayer, MediaPlayer.MEDIA_ERROR_IO, MediaPlayer.MEDIA_ERROR_UNKNOWN);

        }
        catch (IllegalStateException e)
        {
            Log.e(MEDIA_PLAYER, "IllegalStateException: " + e.toString());

            errorMediaPlayer(mMediaPlayer, MediaPlayer.MEDIA_ERROR_IO, MediaPlayer.MEDIA_ERROR_UNKNOWN);
        }
        catch (IllegalArgumentException e)
        {
            Log.e(MEDIA_PLAYER, "IllegalArgumentException: " + e.toString());

            errorMediaPlayer(mMediaPlayer, MediaPlayer.MEDIA_ERROR_IO, MediaPlayer.MEDIA_ERROR_UNKNOWN);
        }

        //update current state
        currentState= State.INITIALIZED;

    }

    /**
     * Put the MediaPlayer in the INITIALIZED state (if is not) and  asynchronously prepare it for the playback
     */
    private final void prepareAsyncMediaPlayer()
    {
        // initializing mMediaPlayer if in a state  different from  INITIALIZED
        if(currentState!= State.INITIALIZED)
        {
            if(currentState!= State.NULL && currentState!= State.IDLE)
            {
                mMediaPlayer.reset();
            }
            configureMediaPlayer();
        }

        // Preparing the player for playback, asynchronously.
        mMediaPlayer.prepareAsync();

        //update current state
        currentState= State.PREPARING;
    }

    /**
     * Put the MediaPlayer in the STOP status
     */
    private final void  stopMediaPlayer()
    {
        if (mMediaPlayer!= null)
        {
            // Stopping streaming
            mMediaPlayer.stop();

            currentState= State.STOPPED;
        }
        else
        {
            currentState=State.NULL;
        }
    }

    /**
     * Handle the errors
     * @param arg0 the MediaPlayer the error pertains to
     * @param arg1 the type of error that has occurred:
     * @param arg2 an extra code, specific to the error. Typically implementation dependent.
     */
    private final void errorMediaPlayer(final MediaPlayer arg0, final int arg1, final int arg2)
    {
        currentState= State.ERROR;

        onError(arg0, arg1, arg2);
    }

    /**
     * Invoked when the media source is ready for playback
     * @param mediaPlayer MediaPlayer ready to be started
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        currentState= State.PREPARED;

        // The MediaPlayer is ready
        mediaPlayer.start();

        currentState= State.STARTED;

        MainActivity.notifyWhenReady();

    }

    /**
     * Called to indicate an error
     * @param arg0 the MediaPlayer the error pertains to
     * @param arg1 the type of error that has occurred:
     * @param arg2 an extra code, specific to the error. Typically implementation dependent.
     * @return True if the method handled the error, false if it didn't. Returning false, or not having an OnErrorListener at all, will cause the OnCompletionListener to be called
     */
    @Override
    public boolean onError(final MediaPlayer arg0, final int arg1, final int arg2)
    {
        configureMediaPlayer();

        //Error handled
        return true;
    }

    /**
     * API to start the MediaPlayer
     */
    protected final void start()
    {
        prepareAsyncMediaPlayer();
    }

    /**
     * API to stop the MediaPlayer
     */
    protected final void stop()
    {
        stopMediaPlayer();
    }

    /**
     * API to destroy the MediaPlayer
     */
    protected final void onDestroy()
    {
        if(mMediaPlayer!=null)
        {
            mMediaPlayer.release();
        }
    }

    /**
     * API to get the current status of the MediaPlayer
     * @return true if the MediaPlayer is streaming, false otherwise
     */
    protected final boolean getStatus()
    {
        if(currentState == State.STARTED)
        {
            return true;
        }

        return false;
    }
}
