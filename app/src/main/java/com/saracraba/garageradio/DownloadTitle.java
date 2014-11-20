package com.saracraba.garageradio;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Thread.sleep;

/**
 * @see AsyncTask on Android
 */
public class DownloadTitle extends AsyncTask<String, String, Void>
{
    private final static String DOWNLOAD_TITLE="Download Title";

    /**
     * @see AsyncTask - doInBackground on Android
     *
     * @param url html page to get the title
     */
    protected Void doInBackground(String... url)
    {
        String response = "";

        // Checking for the new title
        while(MainActivity.refreshTitle)
        {
            try {
                URL urlm = new URL(url[0]);
                URLConnection conn = urlm.openConnection();
                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s = "";

                while ((s = rd.readLine()) != null)
                {
                    response += s;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //check void a null string
            if(response.length()> 0)
            {
                // Decoding string
                String decodedString = Html.fromHtml(response).toString();

                int indexOfSong = decodedString.indexOf("song");
                int indexOfUrl= decodedString.indexOf("url");

                //check if the index of "song+8" is lower than the index of "url-3"
                if(decodedString.length()>indexOfSong+8 && indexOfSong+8<indexOfUrl-3)
                {
                    // Catting the title part of the javascript variable
                    String cattedString = decodedString.substring(indexOfSong + 8,indexOfUrl - 3);

                    // Refreshing text view
                    if (cattedString.contains("- ")) {
                        String[] splitTitleFromArtist = cattedString.split("- ");
                        String titleSong = splitTitleFromArtist[0];
                        String artistSong = splitTitleFromArtist[1];

                        response = titleSong + '\n' + artistSong;
                    }
                }
            }
            publishProgress(response);

            // Sleeping SECOND_TITLE_REFRESH seconds
            try
            {
                sleep(10000);
            }
            catch (InterruptedException e)
            {
                Log.e(DOWNLOAD_TITLE, e.toString());
            }
        }
        return null;
    }

    /**
     * @see AsyncTask - onProgressUpdate on Android
     */
    protected void onProgressUpdate(String... title)
    {
        // Refreshing the user interface title texview
        MainActivity.refreshTitleView(title[0]);
    }

}
