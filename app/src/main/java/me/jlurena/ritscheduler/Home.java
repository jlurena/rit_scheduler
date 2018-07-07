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
import me.jlurena.ritscheduler.serializers.NetworkManager;
import me.jlurena.ritscheduler.serializers.ResponseListener;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final DataManager dataManager = DataManager.getInstance(this);
        final List<Course> cs = new ArrayList<>();
        NetworkManager.getInstance(this).queryCourses("CSCI 250", "2181", new ResponseListener<List<Course>>() {
            @Override
            public void getResult(List<Course> courses, int statusCode, VolleyError error) {
                for (Course course : courses) {
                    try {
                        dataManager.addModel(course);
                    } catch (CouchbaseLiteException e) {
                        Log.d("ERROR", "Something went wrong", e);
                    }
                }
                cs.addAll(courses);
                try {
                    dataManager.getModel(cs.get(0).getModelId(), cs.get(0).modelType, Course.class, new DataManager.DataParser<Course>() {
                        @Override
                        public void parseModel(Course course) {
                            Log.d("Course", "Course");
                        }
                    });
                } catch (CouchbaseLiteException e) {
                    Log.d("ERROR", "Something went wrong", e);
                }
            }
        });


    }
}
