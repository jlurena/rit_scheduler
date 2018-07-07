package me.jlurena.ritscheduler;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.couchbase.lite.CouchbaseLiteException;

import java.util.ArrayList;
import java.util.List;

import me.jlurena.ritscheduler.database.DataManager;
import me.jlurena.ritscheduler.models.Course;
import me.jlurena.ritscheduler.networking.NetworkManager;
import me.jlurena.ritscheduler.networking.ResponseListener;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
