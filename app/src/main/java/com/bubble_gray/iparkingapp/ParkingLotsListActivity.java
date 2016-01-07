package com.bubble_gray.iparkingapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;


public class ParkingLotsListActivity extends ActionBarActivity {
    private ArrayList<HashMap<String, String>> list;
    private ProgressDialog alertProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lots_list);

        alertProgress = new ProgressDialog(ParkingLotsListActivity.this);
        alertProgress.setMessage("Loading...\nPlease wait~~~");
        alertProgress.setCancelable(false);

        list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("address", "台南市東區長榮路三段");
        item.put("number", "4");
        item.put("distance", "300");
        list.add(item);
        HashMap<String, String> item2 = new HashMap<String, String>();
        item.put("address", "台南市東區大學路");
        item.put("number", "1");
        item.put("distance", "450");
        list.add(item2);
        HashMap<String, String> item3 = new HashMap<String, String>();
        item.put("address", "台南市東區小東路");
        item.put("number", "2");
        item.put("distance", "1050");
        list.add(item3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parking_lots_list, menu);
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

    private class ShowParkingLotsList extends AsyncTask<Void, Integer, Long> {

        @Override
        protected void onPreExecute() {
            alertProgress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected Long doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            alertProgress.dismiss();
        }
    }
}
