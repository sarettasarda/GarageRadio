package com.saracraba.garageradio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Sara Craba
 */
public class FirstPageActivity extends Activity {

    protected static NetworkManager networkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        networkManager= new NetworkManager(this);
    }

    public void onGarageRadioButtonClicked(View v)
    {
        // Checking internet connection
        if(!networkManager.getStatus())
        {
            // Making a toast to indicate there is not a internet connection
            Toast.makeText(getApplicationContext(), NetworkManager.ERROR_MESSAGE, Toast.LENGTH_LONG).show();

            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
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
}
