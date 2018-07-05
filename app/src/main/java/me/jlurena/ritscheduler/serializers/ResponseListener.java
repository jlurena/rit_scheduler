package me.jlurena.ritscheduler.serializers;

import com.android.volley.VolleyError;

public interface ResponseListener<T> {

    void getResult(T object, int errorCode, VolleyError error);
}
