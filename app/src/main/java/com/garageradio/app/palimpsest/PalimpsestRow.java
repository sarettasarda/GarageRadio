/**
 * Class PalimpsestRow implements a palimpsest row in the palimpsest view of Garage Radio application
 *
 * @author Sara Craba
 * @since 4/28/14
 * @version 2.0
 */
package com.garageradio.app.palimpsest;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.garageradio.app.R;

public class PalimpsestRow extends LinearLayout
{
    // layout objects
    private TextView mTimeTextView;
    private TextView mNameTextview;

    public PalimpsestRow(Context context, String time, String name)
    {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.palimsest_row, this, true);

        // Initializing layout objects
        mTimeTextView =(TextView) findViewById(R.id.palimpsest_row_time);
        mNameTextview =(TextView) findViewById(R.id.palimpsest_row_name);

        // Setting layout objects
        mTimeTextView.setText(time);
        mNameTextview.setText(name);
    }
}

