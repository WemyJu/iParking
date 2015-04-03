package com.bubble_gray.iparkingapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;


public class Search extends ActionBarActivity {

    private final static String TAG="HelloWorld";
    private ImageButton gohomeBtn,refreshBtn;
    private TextView resultText;
    private Thread askThread;
    private int id;
    private Handler UIHr;
    private AskServer askServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //------get user id-----------
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();//��oBundle
        id=bundle.getInt("ID"); 			//��XBundle���e
        //-------connect button---------
        gohomeBtn=(ImageButton)findViewById(R.id.search_gohome);
        refreshBtn=(ImageButton)findViewById(R.id.search_refresh);
        resultText=(TextView)findViewById(R.id.search_result_Text);

        //-------connect server---------
        askServer=new AskServer();
        askThread=new Thread(askServer);
        askThread.start();

        //-------set button event---------

        gohomeBtn.setOnClickListener(new View.OnClickListener()
                                     {
                                         @Override
                                         public void onClick(View v)
                                         {
                                             Intent recordIntent=new Intent(Search.this,HomePage.class);
                                             startActivity(recordIntent);
                                         }

                                     }
        );

        refreshBtn.setOnClickListener(new View.OnClickListener()
                                      {
                                          @Override
                                          public void onClick(View v)
                                          {

                                          }

                                      }
        );
        //-------set ui----------
        UIHr=new Handler();

    }
    private Runnable refreshUI=new Runnable()
    {
        public void run()
        {
            String res=askServer.getResult();
            if(res.equals("-1"))
                resultText.setText("\nsearching...\n\n");
            else if(res.equals("0") || res.equals("1") || res.equals("2"))
                resultText.setText("\n�Ѿl  "+res+"  ��\n\n");
            else
                resultText.setText("\nno data\n\n");
        }

    };


    class AskServer implements Runnable
    {
        Socket socket;
        OutputStream out;
        InputStream in;
        byte[] strb = new byte[200];
        int result=-1;
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Log.v(TAG, "connect to server...");
                socket=new Socket("140.116.246.200",4000+id);
                out=socket.getOutputStream();
                in=socket.getInputStream();
                Log.v(TAG,"connect!!");

                sendData("ask#");
                UIHr.post(refreshUI);
                result=new Integer(readData());
                UIHr.post(refreshUI);
                sendData("over#");
                socket.close();
            } catch (UnknownHostException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}
        }
        private void sendData(String s)
        {
            Log.v(TAG,"send "+s);
            Arrays.fill(strb, (byte) 0);
            System.arraycopy(s.getBytes(), 0, strb, 0, s.length());
            try
            {
                if(out!=null)
                {
                    out.write(strb);
                    out.flush();
                }
            } catch (IOException e1) {}
        }
        private String readData()
        {
            Log.v(TAG,"connect!!");
            String s = "";
            String[] splits = null;
            Arrays.fill(strb, (byte)0);
            try
            {
                if(in!=null)
                {
                    int count=0;
                    while(in.available()==0 && count<100)
                    {
                        UIHr.post(refreshUI);
                        Thread.sleep(100);
                        count++;
                    }
                    if(count>50)
                    {
                        return new Integer(9999).toString();
                    }

                    Log.v(TAG,"read start");
                    in.read(strb);
                    Log.v(TAG,"read end");

                }
                s=new String(strb);
                splits=s.split("#");
                Log.v(TAG,"get "+splits[0]);

            } catch (IOException e1) {} catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return splits[0];
        }
        private String getResult()
        {
            return new Integer(result).toString();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search,
                    container, false);
            return rootView;
        }
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.v(TAG,"SearchOnPause");
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(TAG,"SearchOnStop");
        android.os.Process.killProcess(android.os.Process.myPid());
        onDestroy();
    }
    protected void onDestroy(Bundle savedInstanceState)
    {
        Log.v(TAG,"SearchOnDestroy");

        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
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
}
