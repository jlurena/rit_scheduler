package me.jlurena.ritscheduler.database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.SelectResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.jlurena.ritscheduler.models.BaseModel;

import static me.jlurena.ritscheduler.database.Contracts.BaseModel.MODEL_ID_KEY;
import static me.jlurena.ritscheduler.database.Contracts.BaseModel.MODEL_TYPE_KEY;

public class DataManager {

    private static DataManager dataManager;
    private static String TAG = "DataManager";
    private static String DB_NAME = "ritscheduler";
    private Database database;


    private DataManager(Context context) {
        DatabaseConfiguration config = new DatabaseConfiguration(context);
        try {
            database = new Database(DB_NAME, config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    public static synchronized DataManager getInstance(Context context) {
        if (dataManager == null) {
            dataManager = new DataManager(context);
        }
        return dataManager;
    }

    public static synchronized DataManager getInstance() {
        if (null == dataManager) {
            throw new IllegalStateException(DataManager.class.getSimpleName() + " is not initialized");
        }
        return dataManager;
    }

    public void addModel(BaseModel model) throws CouchbaseLiteException {
        MutableDocument document = new MutableDocument();
        document.setData(model.toMap());
        document.setString(MODEL_TYPE_KEY, model.modelType);
        document.setString(MODEL_ID_KEY, model.getModelId());
        database.save(document);
    }

    /**
     * Retrieves all properties of document that matches the modelId and type. It is then casted
     * into objectType and passed to the DataParser @see {@link DataParser#parseModel(Object)}.
     *
     * @param modelId    The id representing the model object. <b>NOT</b> the ID of the document.
     * @param modelType  The type name of the model.
     * @param objectType The class type of the object representing the model.
     * @param dataParser The
     * @throws CouchbaseLiteException
     */
    public void getModel(String modelId, String modelType, Class objectType, DataParser dataParser) throws CouchbaseLiteException {
        Query query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property(MODEL_TYPE_KEY).equalTo(Expression.string(modelType))
                        .and(Expression.property(MODEL_ID_KEY).equalTo(Expression.string(modelId))));
        Dictionary objectDictionary = query.execute().allResults().get(0).getDictionary(0);
        //noinspection unchecked
        dataParser.parseModel(new ObjectMapper().convertValue(objectDictionary.toMap(), objectType));
    }

    public interface DataParser<T> {

        void parseModel(T objectMap);
    }
}
