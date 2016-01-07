package HttpRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2016/1/7.
 */
public class PostParameterJsonObjectRequest extends Request {

    private List<BasicNameValuePair> params;        // the request params
    private Response.Listener listener; // the response listener

    public PostParameterJsonObjectRequest(int requestMethod, String url, List params, Response.Listener responseListener, Response.ErrorListener errorListener) {

        super(requestMethod, url, errorListener); // Call parent constructor
        this.params = params;
        this.listener = responseListener;
    }

    // We HAVE TO implement this function
    @Override
    protected void deliverResponse(Object response) {
        listener.onResponse(response); // Call response listener
    }

    // Proper parameter behavior
    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<String, String>();

        // Iterate through the params and add them to our HashMap
        for (BasicNameValuePair pair : params) {
            map.put(pair.getName(), pair.getValue());
        }

        return map;
    }

    // Same as JsonObjectRequest#parseNetworkResponse
    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}