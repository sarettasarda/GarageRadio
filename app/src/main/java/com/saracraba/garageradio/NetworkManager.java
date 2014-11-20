package com.saracraba.garageradio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sara Craba
 */
public class NetworkManager {
    private final ConnectivityManager connectivityManager;
    protected final static String ERROR_MESSAGE= "Connessione di rete non disponibile\n    abilita il wifi o i dati e riprova";

    private NetworkInfo networkInfo;

    /**
     * Initialize the network manager with the CONNECTIVITY_SERVICE
     *
     * @param context application context where to get the connectivity service. Throw a NullPointerException
     *                if context is null.
     *
     */
    protected NetworkManager(Context context)
    {
        if(context== null)
        {
            throw new NullPointerException();
        }
        this.connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    /**
     * Checks for internet connection
     *
     * @return  true if a internet connection is available false otherwise
     */
    public boolean getStatus()
    {
        networkInfo= connectivityManager.getActiveNetworkInfo();

        // Check if there is internet connection
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        }

        return false;
    }

}
