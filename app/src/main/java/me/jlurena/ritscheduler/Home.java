package me.jlurena.ritscheduler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.List;

import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.serializers.NetworkManager;
import me.jlurena.ritscheduler.serializers.ResponseListener;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        NetworkManager.getInstance(this).queryCourses("CSCI 250", "2181", new ResponseListener<List<Course>>() {
            @Override
            public void getResult(List<Course> object, int statusCode, VolleyError error) {
                Log.d("HOME", object.get(0).toMap().toString());
            }
        });
    }
}
