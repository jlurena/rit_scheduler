package me.jlurena.ritscheduler.database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;

import java.io.File;
import java.util.Map;

import me.jlurena.ritscheduler.models.BaseModel;
import me.jlurena.ritscheduler.models.Course;

public class DataManager {

    private Database database;
    private static DataManager dataManager;
    private static String TAG = "DataManager";
    private static String DB_NAME = "ritscheduler";
    private static String TYPE_KEY = "type";

    private DataManager(Context context) {
        DatabaseConfiguration config = new DatabaseConfiguration(context);
        try {
            database = new Database("DB_NAME", config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    public static DataManager getInstance(Context context) {
        if (dataManager == null) {
            dataManager = new DataManager(context);
        }
        return dataManager;
    }

    public boolean addCourseDocument(BaseModel model) {
        MutableDocument document = new MutableDocument(model.getId());
        document.setString(TYPE_KEY, model.getType());
        document.setData(model.toMap());

        return false;
    }

}
