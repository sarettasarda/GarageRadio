/**
 * Class PalimpsestActivity is the palimpsest view of Garage Radio application
 * In this activity user can check the palimpsest of the radio
 *
 * @author Sara Craba
 * @since 4/27/14
 * @version 2.0
 */
package com.garageradio.app.palimpsest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.garageradio.app.R;
import com.garageradio.app.mainpage.MainActivity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @see Activity on Android
 */
public class PalimpsestActivity extends Activity
{
    private static final String URL_PALIMPSEST= "http://www.garageradio.it/palinsesto.html";

    // thread to download the palimpsest html page
    private DownloadPalimpsest downloadPalimpsest= new DownloadPalimpsest();
    private Map<Integer, Map<Integer, String>> weektable;

    // layout object
    private LinearLayout mPalimpsestLinearLayout;

    /**
     * @see onCreate() on Android Activity class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palimpsest);

        // Initializing layout object
        mPalimpsestLinearLayout= (LinearLayout) findViewById(R.id.palimpsestView);

        // Creating palimpsest view
        fillPalimpsest();
    }

    /**
     * Download the palimpsest from the html page and create the view to show it
     */
    private void fillPalimpsest()
    {
        try
        {
            // Downloading palimpsest
            weektable= downloadPalimpsest.execute(URL_PALIMPSEST).get();
        }
        catch (InterruptedException e)
        {
            Log.e(MainActivity.PALIMPSEST_ACTIVITY, e.toString());
        }
        catch (ExecutionException e)
        {
            Log.e(MainActivity.PALIMPSEST_ACTIVITY, e.toString());
        }

        // Creating day of the week label
        TextView dayOfTheWeek;
        PalimpsestRow rowTimeName;

        for(int i=0; i<7; i++)
        {
            if(weektable.containsKey(i))
            {
                // Adding day of the week label
                dayOfTheWeek= new TextView(this);
                dayOfTheWeek.setTextSize(20);
                dayOfTheWeek.setText(convertDayOfTheWeek(i) + ":");
                mPalimpsestLinearLayout.addView(dayOfTheWeek);

                // Getting palimpsest time-name list
                Map<Integer, String> dayList=  weektable.get(i);

                for(int j=0; j<24; j++)
                {
                    if(dayList.containsKey(j))
                    {
                        // Adding time-name row
                        rowTimeName= new PalimpsestRow(this, convertTimeOdTheDay(j),dayList.get(j));
                        mPalimpsestLinearLayout.addView(rowTimeName);
                    }
                }
            }
        }
    }

    /**
     * Converts time of the day from number (0-23) to string
     */
    private String convertTimeOdTheDay(int timeInteger)
    {
        String timeString= null;

        if(timeInteger==9)
        {
            timeString= "09:00 - 10:00";
        }
        else if(timeInteger<10)
        {
            timeString= "0"+timeInteger+":00 - 0"+(timeInteger+1)+":00";
        }
        else
        {
            timeString= timeInteger+":00 - "+(timeInteger+1)+":00";
        }

        return timeString;
    }

    /**
     * Converts day of the week from number (0-6) to string
     */
    private String convertDayOfTheWeek(int dayInteger)
    {
        String dayString=null;

        switch (dayInteger)
        {
            case 0:
                dayString="Lunedì";
                break;
            case 1:
                dayString="Martedì";
                break;
            case 2:
                dayString="Mercoledì";
                break;
            case 3:
                dayString="Giovedì";
                break;
            case 4:
                dayString="Venerdì";
                break;
            case 5:
                dayString="Sabato";
                break;
            case 6:
                dayString="Domenica";
                break;
        }

        return dayString;
    }
}
