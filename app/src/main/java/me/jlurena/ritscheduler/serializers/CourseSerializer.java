package me.jlurena.ritscheduler.serializers;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.jlurena.ritscheduler.models.Course;

/**
 * serializers.CourseSerializer.java
 * Serializer for Course.
 */
public class CourseSerializer {
    public static final String SEARCHRESULTS = "searchResults";

    public static Course toCourse(JSONObject json) {
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Course.class);
    }

    public static List<Course> toCourseResults(JSONObject json) throws JSONException {
        ArrayList<Course> courses = new ArrayList<>();
        Gson gson = new Gson();
        JSONArray coursesJsonResults = json.getJSONArray(SEARCHRESULTS);
        for (int i = 0; i < coursesJsonResults.length(); i++) {
            courses.add(gson.fromJson(coursesJsonResults.get(i).toString(), Course.class));
        }

        return courses;
    }

    public static JSONObject buildJSONMapParameter(String query, String term) throws JSONException {
        final int rows = 500;
        final String career = "";
        JSONObject json = new JSONObject();
        json.put("query", query);
        json.put("rows", rows);
        json.put("term", term);
        json.put("career", career);
        return json;
    }
}
