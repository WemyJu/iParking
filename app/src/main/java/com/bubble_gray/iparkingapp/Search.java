package com.bubble_gray.iparkingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import http_request.AppController;
import http_request.PostParameterJsonObjectRequest;


public class Search extends ActionBarActivity{

    private final static String TAG="HelloWorld";
    private ImageButton gohomeBtn,refreshBtn;

    private ArrayList<HashMap<String, String>> list;
    private ProgressDialog alertProgress;
    private ListView listview;
    private SimpleAdapter adapter;

    private LocationManager lm;

    private String GPSlat, GPSlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //-------connect button---------
        gohomeBtn=(ImageButton)findViewById(R.id.search_gohome);
        refreshBtn=(ImageButton)findViewById(R.id.search_refresh);

        //-------set button event---------

        gohomeBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Search.this.finish();
                                         }

                                     }
        );

        refreshBtn.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              new ShowParkingLotsList().execute();
                                          }

                                      }
        );

        //-------set ui----------
        alertProgress = new ProgressDialog(Search.this);
        alertProgress.setMessage("Loading...\nPlease wait~~~");
        alertProgress.setCancelable(false);

        list = new ArrayList<HashMap<String, String>>();
        listview = (ListView) this.findViewById(R.id.listView);
        adapter = new SimpleAdapter(this, list, R.layout.parking_lots_list, new String[]{"address", "number", "distance"}, new int[]{R.id.address_tv, R.id.num_tv, R.id.distance_tv});
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent it = new Intent(Search.this, GoogleMap.class);
                it.putExtra("GPS", list.get(position).get("gps"));
                it.putExtra("describe", list.get(position).get("address") + " " + list.get(position).get("number") + "個車位");
                Log.w("in search ", list.get(position).get("gps"));
                startActivity(it);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
        list.clear();
        new ShowParkingLotsList().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Long doInBackground(Void... voids) {
            String tag_json_obj = "json_obj_req";
            String url = "http://140.116.245.252:8080/api/searchSpace";

            ArrayList<BasicNameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("GPSlat", GPSlat));
            params.add(new BasicNameValuePair("GPSlng", GPSlng));
            PostParameterJsonObjectRequest jsonObjReq = new PostParameterJsonObjectRequest(Request.Method.POST, url , params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());

                    try {
                        JSONObject result = response.getJSONObject("result");
                        Log.d(TAG, result.toString());
                        Iterator<String> iter = result.keys();

                        while (iter.hasNext()) {
                            String address = iter.next();
                            JSONObject data = result.getJSONObject(address);
                            String number = data.getString("count");
                            String distance = data.getString("dis");
                            String gps = data.getString("GPSlat") + "," + data.getString("GPSlng");

                            HashMap<String, String> item = new HashMap<String, String>();
                            item.put("address", address);
                            item.put("number", number);
                            item.put("distance", distance);
                            item.put("gps", gps);
                            list.add(item);
                        }

                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                    adapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            alertProgress.dismiss();
        }
    }


    private void locationServiceInitial() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
        String bestProvider = lm.getBestProvider(new Criteria(), true);
        Location location = lm.getLastKnownLocation(bestProvider);	//使用GPS定位座標
        GPSlng = String.valueOf(location.getLongitude())+"";
        GPSlat = String.valueOf(location.getLatitude())+"";
    }
}
