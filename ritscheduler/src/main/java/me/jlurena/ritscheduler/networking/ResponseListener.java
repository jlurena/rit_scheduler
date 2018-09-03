package me.jlurena.ritscheduler.networking;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;

public interface ResponseListener<T> {

    void getResult(@NonNull T object, int errorCode, VolleyError error);

    void onRequestFinished();
}
