/**
 * Class DownloadPalimpsest is a thread to download palimpsest from a url and decode it from html to a Map
 *
 * @author Sara Craba
 * @since 4/28/14
 * @version 2.0
 */
package com.garageradio.app.palimpsest;

import android.os.AsyncTask;
import android.util.Log;

import com.garageradio.app.mainpage.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @see AsyncTask on Android
 */
public class DownloadPalimpsest extends AsyncTask<String , Void, Map>
{
    /* <map explanation>
     *
     * row= time
     * column = day of the week
     *
     * onDay<time, name>
     *      time 0= 00:00 - 01:00
     *      time 1= 01:00 - 02:00
     *      time 2= 02:00 - 03:00
     *      time 3= 03:00 - 04:00
     *      ..
     *      time 23= 23:00 -24:00
     *
     * weektable< weekday, < time, name> >
     *     example: < 2(wednesday) < 4(04:00-05:00) , LUPO> >
     */
    Map<Integer, Map<Integer, String>> weektable= new HashMap<Integer, Map<Integer, String>>();
    Map<Integer, String> onMonday = new HashMap<Integer, String>();
    Map<Integer, String> onTuesday= new HashMap<Integer, String>();
    Map<Integer, String> onWednesday= new HashMap<Integer, String>();
    Map<Integer, String> onThursday= new HashMap<Integer, String>();
    Map<Integer, String> onFriday= new HashMap<Integer, String>();
    Map<Integer, String> onSaturday= new HashMap<Integer, String>();
    Map<Integer, String> onSunday= new HashMap<Integer, String>();

    /**
     * @see AsyncTask - doInBackground on Android
     *
     * @param url html page to get the palimpsest
     *
     * @return map of the palimpsest
     */
    protected Map doInBackground(String... url)
    {
        Document doc = null;

        // Downloading html
        try
        {
            doc = Jsoup.connect(url[0]).get();
        }
        catch (IOException e)
        {
            Log.e(MainActivity.DOWNLOAD_PALIMPSEST, e.toString());
        }

        // Parsing html table
        for (Element table : doc.select("table"))
        {
            Elements rows= table.select("tr");

            for(int i=2; i< rows.size(); i++)
            {
                Element row_i = rows.get(i);
                Elements columns = row_i.select("td");

                for(int j=1; j< columns.size(); j++)
                {
                    Element column_j= columns.get(j);
                    Elements pSelector= column_j.select("p");

                    if(pSelector != null)
                    {
                        Elements spanSelector= pSelector.select("span");

                        if(spanSelector != null)
                        {
                            String text= spanSelector.text();

                            if(!text.equals("Selezione Musicale"))
                            {
                                switch(j)
                                {
                                    case 1: //Monday
                                        onMonday.put(i-2, text);
                                        break;
                                    case 2: //Tuesday
                                        onTuesday.put(i-2, text);
                                        break;
                                    case 3: //Wednesday
                                        onWednesday.put(i-2, text);
                                        break;
                                    case 4: //Thursday
                                        onThursday.put(i-2, text);
                                        break;
                                    case 5: //Friday
                                        onFriday.put(i-2, text);
                                        break;
                                    case 6: //Saturday
                                        onSaturday.put(i-2, text);
                                        break;
                                    case 7: //Sunday
                                        onSunday.put(i-2, text);
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filling the map
        if(onMonday.size()>0)
        {
            weektable.put(0, onMonday);
        }
        if(onTuesday.size()>0)
        {
            weektable.put(1, onTuesday);
        }
        if(onWednesday.size()>0)
        {
            weektable.put(2, onWednesday);
        }
        if(onThursday.size()>0)
        {
            weektable.put(3, onThursday);
        }
        if(onFriday.size()>0)
        {
            weektable.put(4, onFriday);
        }
        if(onSaturday.size()>0)
        {
            weektable.put(5, onSaturday);
        }
        if(onSunday.size()>0)
        {
            weektable.put(6, onSunday);
        }

        return weektable;
    }
}
