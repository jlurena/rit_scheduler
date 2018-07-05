package me.jlurena.ritscheduler.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jlurena.ritscheduler.models.Course;

/**
 * serializers.CourseSerializer.java
 * Serializer for Course.
 */
public class CourseSerializer {
    public static final String SEARCHRESULTS = "searchResults";

    public static List<Course> toCourseResults(JSONObject json) throws IOException, JSONException {
        ArrayList<Course> courses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JSONArray coursesJsonResults = json.getJSONArray(SEARCHRESULTS);
        for (int i = 0; i < coursesJsonResults.length(); i++) {
            courses.add(objectMapper.readValue(coursesJsonResults.get(i).toString(), Course.class));
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
