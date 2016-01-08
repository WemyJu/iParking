package http_request;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by mac on 2016/1/6.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }


   /* public Request<?> api_logout(String psnCode,String phone_id,Listener<JSONObject> listener, ErrorListener errorListener) {

        String uri = String.format(Constants.API_LOGOUT_URL);

        if(D_API_ACTIVITY_FUTURE) {
            Log.w(TAG,"api_setPhoneID uri = " + uri);
            Log.w(TAG,"api_delPhoneID = " + phone_id +" stuno = "+ psnCode);
        }

        ArrayList<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("stu_no"    , psnCode));
        params.add(new BasicNameValuePair("device_type"    , "A"));
        params.add(new BasicNameValuePair("device_id"    , phone_id));
        params.add(new BasicNameValuePair("senderid", "671711953163"));
        PostParameterJsonObjectRequest jsonObjectRequest = new PostParameterJsonObjectRequest(Method.POST, uri , params, listener, errorListener) ;

        return mQueue.add(jsonObjectRequest);
    }*/

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}