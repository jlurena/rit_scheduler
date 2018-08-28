package me.jlurena.ritscheduler.networking;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.jlurena.ritscheduler.models.Course;

/**
 * Manages network tasks including API calls.
 */
public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private final static AtomicInteger requestCounter = new AtomicInteger();
    private static NetworkManager instance = null;
    private final RequestQueue requestQueue;

    /**
     * Instantiate a NetworkManager with the application's context.
     *
     * @param context Application's context.
     */
    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Builds and encodes a URL.
     *
     * @param queryUrl URL path.
     * @param parameters Parameters of URL.
     * @return An encoded URL.
     */
    private String buildUrl(String queryUrl, String parameters) {
        String url = null;
        try {
            url = queryUrl + URLEncoder.encode(parameters, "utf-8");
        } catch (UnsupportedEncodingException e) {
            //            Log.e(TAG, "Error building URL", e);
        }

        return url;
    }

    /**
     * Get instance of NetworkManager.
     *
     * @param context Application's context.
     * @return The instance of NetworkManager.
     */
    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    /**
     * Get the existing instance of NetworkManager.
     *
     * @return The NetworkManager instance.
     * @throws IllegalStateException When a NetworkManager instance has not yet been instantiated.
     */
    public static synchronized NetworkManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() + " is not initialized");
        }
        return instance;
    }

    /**
     * Getter of the RequestQueue.
     *
     * @return The RequestQueue.
     */
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void queryAutoComplete(String query, final ResponseListener<List<String>> responseListener) {
        String url = buildUrl(Serializers.AUTO_COMPLETE_URL, query);
        requestCounter.incrementAndGet();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                responseListener.getResult(Serializers.toAutoCompleteList(response), 200, null);
            } catch (JSONException e) {
                //                Log.e(TAG, "Error parsing response", e);
            }
        }, error -> {
            //            Log.e(TAG, error.getMessage());
            int status = error.networkResponse == null ? 500 : error.networkResponse.statusCode;
            responseListener.getResult(null, status, error);
        });

        requestQueue.add(request);
        requestQueue.addRequestFinishedListener(r -> {
            requestCounter.decrementAndGet();
            if (requestCounter.get() == 0) {
                responseListener.onRequestFinished();
            }
        });
    }

    /**
     * Sends GET request query call to TigerCenter API to search for a Course.
     *
     * @param query Query term.
     * @param term Term as a coded numerical String.
     * @param responseListener ResponseListener callback after response from API is received.
     */
    public void queryCourses(String query, String term, final ResponseListener<List<Course>> responseListener) {

        String url = buildUrl(Serializers.COURSE_QUERY_URL, Serializers.buildCourseQueryParameter(query, term).toString());
        requestCounter.incrementAndGet();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                responseListener.getResult(Serializers.toCourseResults(response), 200, null);
            } catch (JSONException | IOException e) {
                //                Log.e(TAG, "Error parsing response", e);
            }
        }, error -> {
            //            Log.e(TAG, error.getMessage());
            int status = error.networkResponse == null ? 500 : error.networkResponse.statusCode;
            responseListener.getResult(null, status, error);
        });

        requestQueue.add(request);
        requestQueue.addRequestFinishedListener(r -> {
            requestCounter.decrementAndGet();
            if (requestCounter.get() == 0) {
                responseListener.onRequestFinished();
            }
        });
    }
}