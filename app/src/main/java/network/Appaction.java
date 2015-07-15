package network;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by mac on 15/7/15.
 */
public class Appaction  extends android.app.Application{
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "sessionid";

    private static Appaction sInstance;

    public static Appaction get() {
        return sInstance;
        //return sInstance
    }

    private final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(20);

    private Web_Service mApi;

    private ImageLoader mImageLoader;

    //private DaoMaster mDaoMaster;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        RequestQueue queue = Volley.newRequestQueue(this, new OkHttpStack());
        mApi = new Web_Service(queue);

        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                mImageCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return mImageCache.get(key);
            }
        };

        mImageLoader = new ImageLoader(queue, imageCache);

//		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Contents.DB_NAME, null);
//		db = helper.getWritableDatabase();
//		mDaoMaster = new DaoMaster(db);
//		mDeviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
//
//		if(mDeviceId == null) {
//			mDeviceId =	Installation.id(this);
//		}

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

//		mAlertDialogBuilder = new AlertDialog.Builder(this);
//		mAlertDialogBuilder.setCancelable(false);

    }

    public Web_Service getApi() {
        return mApi;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /*public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public SharedPreferences getMySharedPreferences(){
        return mSharedPreferences;

    }*/

    // Check network connection
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
