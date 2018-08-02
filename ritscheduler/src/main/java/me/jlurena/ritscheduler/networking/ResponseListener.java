package me.jlurena.ritscheduler.networking;

import com.android.volley.VolleyError;

public interface ResponseListener<T> {

    void getResult(T object, int errorCode, VolleyError error);
    void onRequestFinished();
}
