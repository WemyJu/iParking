package com.bubble_gray.iparkingapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;


public class HomePage extends ActionBarActivity {
    private final static String TAG="HelloWorld";
    private ImageButton recordBtn,searchBtn,helpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //-------connect button---------
        recordBtn=(ImageButton)findViewById(R.id.home_record);
        searchBtn=(ImageButton)findViewById(R.id.home_search);
        helpBtn=(ImageButton)findViewById(R.id.home_help);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        /* already connected to Internet */
        if (( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) &&  (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) ){
            //-------set button event---------
            recordBtn.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View v)
                                             {
                                                 Intent recordIntent=new Intent(HomePage.this,Record.class);
                                                 startActivity(recordIntent);
                                             }

                                         }
            );

            searchBtn.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View v)
                                             {
                                                 Intent searchIntent=new Intent(HomePage.this, Search.class);
                                                 startActivity(searchIntent);
                                             }

                                         }
            );
            helpBtn.setOnClickListener(new View.OnClickListener(){
                                           @Override
                                           public void onClick(View v)
                                           {
                                               Intent instructionPage = new Intent(HomePage.this, Instruction.class);
                                               startActivity(instructionPage);
                                               /*myDB.open();
                                               myDB.deleteDB();
                                               myDB.close();
                                               */
                                           }

                                       }
            );

        }else {
        /* not connected to Internet */
            if (conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                    || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
                AlertDialog.Builder bdr = new AlertDialog.Builder(this);
                bdr.setMessage("Please connect to Internet.")
                        .setTitle("Warning")
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setCancelable(true)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
            if(!status.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                AlertDialog.Builder bdr = new AlertDialog.Builder(this);
                bdr.setMessage("Please turn on GPS service.")
                        .setTitle("Warning")
                        .setIcon(android.R.drawable.ic_menu_info_details)
                        .setCancelable(true)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.v(TAG,"HomeOnPause");
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(TAG,"HomeOnStop");
    }
    protected void onDestroy(Bundle savedInstanceState)
    {
        Log.v(TAG,"HomeOnDestroy");

        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
