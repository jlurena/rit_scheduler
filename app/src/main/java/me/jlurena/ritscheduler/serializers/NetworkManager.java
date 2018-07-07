package me.jlurena.ritscheduler.serializers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import me.jlurena.ritscheduler.models.Course;

public class NetworkManager {
    public static final String QUERY_URL = "https://tigercenter.rit.edu/tigerCenterSearch/api/search?map=";
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;
    private RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null)
            instance = new NetworkManager(context);
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() + " is not initialized");
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void queryCourses(String query, String term, final ResponseListener<List<Course>> responseListener) {

        String url = buildUrl(query, term);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    responseListener.getResult(CourseSerializer.toCourseResults(response), 200, null);
                } catch (JSONException | IOException e) {
                    Log.e(TAG, "Error parsing response", e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage());
                responseListener.getResult(null, error.networkResponse.statusCode, error);
            }
        });
        requestQueue.add(request);
    }

    private String buildUrl(String query, String term) {
        String url = null;
        try {
            url = QUERY_URL + URLEncoder.encode(CourseSerializer.buildJSONMapParameter(query, term).toString(), "utf-8");
        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e(TAG, "Error building URL", e);
        }

        return url;
    }
}