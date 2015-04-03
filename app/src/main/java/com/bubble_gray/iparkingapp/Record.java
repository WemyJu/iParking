package com.bubble_gray.iparkingapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Record extends ActionBarActivity implements LocationListener{

    //=========variable============
    //--------debug---------
    private final static String TAG="HelloWorld";
    Message msg =new Message();
    String errMsg;
    private int id;
    //--------gps---------
    LocationManager lm;
    TextView gpsTv;
    Handler GpsHr,ErrHr,VideoHr;
    GPSCollector gpsCollector;
    boolean isLocationChange;
    SimpleDateFormat dateFormat;
    private Socket gpsSocket;
    static String fileName;
    //--------camera---------
    Button stopRec;
    CamaraRecorder cameraRecorder;
    MediaRecorder recorder;
    android.hardware.Camera myCamera;
    android.hardware.Camera.Parameters para;
    //-------be searched------
    BeSearched beSearched;
    Socket searchedSocket;
    //----UI & control-------
    SurfaceView cameraView;
    Thread recThread;
    Thread gpsThread;
    Thread searchedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        //----choose UI-----
        setContentView(R.layout.activity_record);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();//��oBundle
        id=bundle.getInt("ID"); 			//��XBundle���e
        //----Connect object and UI-----
        stopRec=(Button)findViewById(R.id.stop_btn);
        gpsTv=(TextView)findViewById(R.id.now_gps);
        cameraView=(SurfaceView)findViewById(R.id.surfaceView1);

        //----new object for variable-----
        this.lm = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            String provider=this.lm.getBestProvider(new Criteria(), true);
            this.lm.getLastKnownLocation(/*provider*/LocationManager.GPS_PROVIDER);
            lm.requestLocationUpdates(provider, 1000, 0, (LocationListener) this);
        }
        GpsHr=new Handler();
        ErrHr=new ErrHandler();
        VideoHr=new Handler();
        dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        //----button event-----
        stopRec.setOnClickListener(stopRecLis);

        //----creat thread object-----
        gpsCollector=new GPSCollector(lm);
        gpsThread = new Thread(gpsCollector);
        cameraRecorder=new CamaraRecorder();
        recThread = new Thread(cameraRecorder);
        beSearched=new BeSearched();
        searchedThread=new Thread(beSearched);
        //-----start thread------
        gpsThread.start();
        recThread.start();
        searchedThread.start();
    }

    //=====stop recording and return=======
    private View.OnClickListener stopRecLis=new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Log.v(TAG,"onClick");
            cameraRecorder.stopCamera();
            gpsCollector.sendData("over#");
            recThread.interrupt();
            gpsThread.interrupt();
            onPause();
        }
    };
    //=====update gps & camera UI=======
    private Runnable refreshUI=new Runnable()
    {
        public void run()
        {
            gpsTv.setText(gpsCollector.getGpsLoc());
        }

    };
    private Runnable searchUI=new Runnable()
    {
        public void run()
        {
            gpsTv.setText(beSearched.getInput());
        }

    };
    private Runnable recorderUI=new Runnable()
    {
        public void run()
        {
            gpsTv.setText("starting...");
        }

    };
    private Runnable videoUI=new Runnable()
    {
        public void run()
        {
            String dataPath=getLatestFile();
            //String dataPath = "/storage/emulated/0/DCIM/100MEDIA/Car20150102234724.mp4";
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(dataPath);
            String Videotime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // ��o�v����(ms)
            Log.v(TAG,"video time : "+Videotime);
            Bitmap bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_NEXT_SYNC);
            Log.v(TAG,"get frame");
            String tmpPath =  "/storage/emulated/0/DCIM/100MEDIA/tmp.jpg";
            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(tmpPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();
                Log.v(TAG,"video capture done");
            } catch (Exception e) { e.printStackTrace(); }
        }
        public String getLatestFile()
        {
            String[] filenames;
            String latest="";
            File dir=new File("storage/emulated/0/DCIM/100MEDIA");
            filenames=dir.list();
            for(int i=filenames.length-1;i>=0;--i)
            {
                if(filenames[i].substring(0, 3).equals("Car"))
                {
                    Log.v(TAG,filenames[i]);
                    latest=filenames[i];
                    break;
                }
            }
            return "storage/emulated/0/DCIM/100MEDIA/"+latest;
        }

    };

    //=====error handler=======
    class ErrHandler extends Handler
    {

        public void handleMessage(Message msg1)
        {
            switch(msg1.what)
            {
                case 1:
                    gpsTv.setText("no web");
                    break;
                case 2:
                    gpsTv.setText("Socket IOException");
                    break;
                case 3:
                    gpsTv.setText("Socket OutStream Fail");
                    break;
                case 4:
                    gpsTv.setText("sleep Fail");
                    break;
                case 5:
                    gpsTv.setText("hello");
                    break;
                case 6:
                    gpsTv.setText("record IO error"+errMsg);
                    break;
                default:
                    gpsTv.setText("unknown error");
                    break;
            }
            super.handleMessage(msg1);
        }
    }
    //=====gps=======
    class GPSCollector implements Runnable
    {

        private LocationManager lm;
        private String gpsLoc;
        byte[] strb = new byte[200];
        String str;
        OutputStream out;
        InputStream in;
        //!!!!!!!!!
        public GPSCollector(LocationManager lm)
        {
            this.lm=lm;
            this.gpsLoc="�y�СG0,0";
            isLocationChange=true;
            showMsg(1000);

        }
        private void showMsg(int msgId)
        {
            msg.what=msgId;
            ErrHr.sendMessage(msg);
        }
        public String getGpsLoc()
        {
            return gpsLoc;
        }
        public void run()
        {
            Log.v(TAG,"GPS run");
            //-----move current thread into the background-----
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            //=====1.Create a socket(socket())=====//
            //=====2.connect to serve(connect())=====//
            try
            {
                gpsSocket=new Socket("140.116.246.200",3000+id);
                out=gpsSocket.getOutputStream();
                in=gpsSocket.getInputStream();
            }
            catch (UnknownHostException e)
            {	showMsg(1);}
            catch (IOException e)
            {	showMsg(2);	}
            //=====3.send and receive data(read() write())=====//
            // [gps]#[data]#

            do
            {
                //gps
                if(isLocationChange)
                {
					/*String provider=this.lm.getBestProvider(new Criteria(), true);
					if (provider == null)
					{
						continue;
					}
					else
					{*/
                    Log.v(TAG,"gps send!!!!");
                    //----get and send new gps and date----
                    String bestProvider = LocationManager.GPS_PROVIDER;
                    Criteria criteria = new Criteria();	//��T���Ѫ̿��з�
                    bestProvider = lm.getBestProvider(criteria, true);	//��ܺ�ǫ׳̰������Ѫ�
                    Location location = this.lm.getLastKnownLocation(/*providerLocationManager.GPS_PROVIDER*/bestProvider);
                    gpsLoc=location.getLongitude() + ","+ location.getLatitude();
                    GpsHr.post(refreshUI);
                    Date current = new Date();
                    str="gps#"+gpsLoc+"#"+dateFormat.format(current)+"#"+fileName+"#";
                    sendData(str);
                    isLocationChange=false;
                    //----wait 5 second----
                    try {	Thread.sleep(3000);	}
                    catch (InterruptedException e)
                    {	showMsg(4);		}

                    //}
                }
            }while(true);
        }

        private void sendData(String s)
        {
            Log.v(TAG,"send "+s);
            Arrays.fill(strb, (byte) 0);
            System.arraycopy(s.getBytes(), 0, strb, 0, s.length());
            try
            {
                if(out!=null)
                    out.write(strb);
            } catch (IOException e1)
            {
                showMsg(3);
            }
        }

        private String readData()
        {
            String s = "";
            String[] splits = null;
            Arrays.fill(strb, (byte)0);
            try
            {
                if(in!=null)
                    in.read(strb);

                s=new String(strb);
                splits=s.split("#");

            } catch (IOException e1)
            {
                showMsg(3);
            }
            return splits[0];
        }


    }

    public void onLocationChanged(Location location)
    {
        Log.v(TAG,"location change~~~~");
        isLocationChange=true;
    }
    //=====recording=======
    class CamaraRecorder implements Runnable
    {
        boolean isFocus;
        String path;
        public void run()
        {
            Log.v(TAG,"Camera run");
            path=createFilePath();
            GpsHr.post(recorderUI);
            isFocus=false;
            myCamera=android.hardware.Camera.open();
            myCamera.stopPreview();
            myCamera.setDisplayOrientation(90);
            para=myCamera.getParameters();
            para.setFocusMode("auto");
            myCamera.setParameters(para);
            myCamera.startPreview();
            //myCamera.autoFocus(focus);
            myCamera.unlock();

            recorder=new MediaRecorder();
            recorder.setCamera(myCamera);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            recorder.setOutputFile(path);
            recorder.setOrientationHint(0);

            recorder.setVideoSize(1920,1080);	//1080p
            recorder.setVideoEncodingBitRate(1000*1024);	//8000 kbps
            // �]�w�w���
            recorder.setPreviewDisplay(cameraView.getHolder().getSurface());


            try
            {
                recorder.prepare();
                recorder.start();
            }
            catch (IOException e)
            {
                msg.what=6;
                errMsg=e.getLocalizedMessage();
                ErrHr.sendMessage(msg);
            }
        }

        private String createFilePath()
        {
            Date current = new Date();
            String pathStr="/storage/emulated/0/DCIM/100MEDIA/Car"+dateFormat.format(current)+".mp4";
            fileName=dateFormat.format(current);
            return pathStr;

        }

        private void stopCamera()
        {
            recorder.stop();
        }

		/*AutoFocusCallback focus= new AutoFocusCallback()
		{
			@Override
			public void onAutoFocus(boolean success,android.hardware.Camera camera)
			{
				// TODO Auto-generated method stub
				if(success)
				{
					camera.setOneShotPreviewCallback(previewCallback);
				}
			}
		};*/


    }
    //======be searched==========
    class BeSearched implements Runnable
    {

        String str;
        InputStream inSearch;
        OutputStream outSearch;
        String tmpPath;
        @Override
        public void run() {
            try {
                searchedSocket=new Socket("140.116.246.200",5000+id);
                inSearch=searchedSocket.getInputStream();
                outSearch=searchedSocket.getOutputStream();
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            while(true)
            {
                try {
                    byte[] strb = new byte[200];
                    inSearch.read(strb);
                    str=new String(strb);
                    Log.v(TAG,"video get "+str);
                    String[] cutData=str.split("#");
                    GpsHr.post(searchUI);
                    //get the be searched time
                    searchVideo(cutData[0],cutData[1]);
                    //searchVideo("20150106070241","2");
                    //VideoHr.post(videoUI);
                    //Thread.sleep(1000);

                    sendPic(cutData[2]);
                    //send back

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        public String getInput()
        {
            return str;
        }
        public void searchVideo(String fn,String secStr) throws InterruptedException
        {
            Log.v(TAG,fn+" "+secStr);
            //String dataPath=getLatestFile();
            String dataPath = "/storage/emulated/0/DCIM/100MEDIA/Car"+fn+".mp4";
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(dataPath);
            String Videotime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // ��o�v����(ms)
            Log.v(TAG,"video time : "+Videotime);
            int sec=new Integer(secStr);
            Long frameNum=new Long(sec*1000000);
            Bitmap bitmap = retriever.getFrameAtTime(frameNum);
            Log.v(TAG,"get frame");
            tmpPath =  "/storage/emulated/0/DCIM/100MEDIA/tmp.jpg";
            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(tmpPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos);
                fos.close();
                Log.v(TAG,"video capture done");
            } catch (Exception e) { e.printStackTrace(); }


        }

        public void sendPic(String port) throws IOException
        {
            int spotNum=new Integer(port);
            Log.v(TAG,"sendPic");
            FileInputStream inputStream=new FileInputStream(tmpPath);
            DataPic data=new DataPic();
            data.setSize(inputStream.available());
            inputStream.read(data.bitmap);
            Socket picSoc=new Socket("140.116.246.200",6000+id*10+spotNum);
            ObjectOutputStream outObj=new ObjectOutputStream(picSoc.getOutputStream());
            outObj.writeObject (data);
            picSoc.getOutputStream().flush();

			/*
			byte[] pic=new byte[200000];

			FileInputStream inputStream=new FileInputStream(tmpPath);
			int fileSize=new Integer(inputStream.available());
			Log.v(TAG,"#"+new Integer(inputStream.available()).toString());
			outSearch.write((new Integer(inputStream.available()).toString()+"#").getBytes());
			outSearch.flush();

			inputStream.read(pic);
			Log.v(TAG,"##"+new Integer(inputStream.available()).toString());
			Log.v(TAG,"read from file");

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			outSearch.write(pic,0,fileSize);
			outSearch.flush();*/
            inputStream.close();
            Log.v(TAG,"send over");
        }
        public String getLatestFile()
        {
            String[] filenames;
            String latest="";
            File dir=new File("storage/emulated/0/DCIM/100MEDIA");
            filenames=dir.list();
            for(int i=filenames.length-1;i>=0;--i)
            {
                if(filenames[i].substring(0, 3).equals("Car"))
                {
                    Log.v(TAG,filenames[i]);
                    latest=filenames[i];
                    break;
                }
            }
            return "storage/emulated/0/DCIM/100MEDIA/"+latest;
        }
		/*
		class DataSet implements java.io.Serializable
		{
			int picSize;
			byte[] bitmap;
			public DataSet(int size)
			{
				this.picSize=size;
				bitmap=new byte[picSize];
			}
			public void setBitmap(byte[] img)
			{
				bitmap=img;
			}
		}*/
    }
    //======Life cycle======
    @Override
    protected void onPause()
    {
        super.onPause();
        Log.v(TAG,"onPause");
        onStop();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.v(TAG,"onStop");
        // ���U�@���A�N�O�n���������
        if (recorder != null)
        {
            recorder.release();
        }

        byte strb[]=new byte[200];
        String s="over#";
        try {
            Arrays.fill(strb, (byte)0);
            System.arraycopy(s.getBytes(), 0, strb, 0, s.length());
            gpsSocket.getOutputStream().write(strb);
            searchedSocket.getOutputStream().write(strb);
            if(gpsSocket!=null) gpsSocket.close();
            if(searchedSocket!=null) searchedSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        android.os.Process.killProcess(android.os.Process.myPid());
        onDestroy();
    }
    protected void onDestroy(Bundle savedInstanceState)
    {
        Log.v(TAG,"onDestroy");
        // ���U�@���A�N�O�n���������
        if (recorder != null)
        {
            Log.v(TAG,"killme");
            recorder.release();
        }
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
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
            View rootView = inflater.inflate(R.layout.fragment_record,
                    container, false);
            return rootView;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
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
