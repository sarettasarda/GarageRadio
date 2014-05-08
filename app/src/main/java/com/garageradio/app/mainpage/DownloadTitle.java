/**
 * Class DownloadTitle is the thread to refresh the title each SECOND_TITLE_REFRESH seconds
 * in the user interface of the main screen
 *
 * @author Sara Craba
 * @since 4/27/14
 * @version 2.0
 */
package com.garageradio.app.mainpage;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

/**
 * @see AsyncTask on Android
 */
public class DownloadTitle extends AsyncTask<String, String, Void>
{
    public AsyncResponse delegate=null;

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
            // Http connection
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url[0]);

            try
            {
                // Getting html page
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";

                // Building the string
                while ((s = buffer.readLine()) != null)
                {
                    response += s;
                }

            }
            catch (Exception e)
            {
                Log.e(MainActivity.DOWNLOAD_TITLE, e.toString());
            }

            // Catting the title part of the html page
            String cattedString= response.substring(response.indexOf("song")+8, response.indexOf("url")-3);

            // Decoding string
            String decodedCattedString=  Html.fromHtml(cattedString).toString();

            // Refreshing text view
            if(decodedCattedString.contains("- "))
            {
                String[] splitTitleFromArtist = decodedCattedString.split("- ");
                String titleSong = splitTitleFromArtist[0];
                String artistSong = splitTitleFromArtist[1];

                publishProgress(titleSong + '\n'+ artistSong);
            }
            else
            {
                publishProgress(decodedCattedString);
            }

            // Sleeping SECOND_TITLE_REFRESH seconds
            try
            {
                sleep(MainActivity.SECOND_TITLE_REFRESH *1000);
            }
            catch (InterruptedException e)
            {
                Log.e(MainActivity.DOWNLOAD_TITLE, e.toString());
            }

            // Cleaning response string
            response = "";

        }
        return null;
    }

    /**
     * @see AsyncTask - onProgressUpdate on Android
     */
    protected void onProgressUpdate(String... title)
    {
        // Refreshing the user interface title texview
        delegate.refreshTitleView(title[0]);
    }

}
