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
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import me.jlurena.ritscheduler.models.Model;

import static me.jlurena.ritscheduler.database.Contracts.BaseModel.MODEL_ID_KEY;
import static me.jlurena.ritscheduler.database.Contracts.BaseModel.MODEL_TYPE_KEY;

/**
 * Singleton class used to retrieve and store documents in the database.
 */
public class DataManager {

    private static final String TAG = "DataManager";
    private static final String DB_NAME = "ritscheduler";
    private static DataManager dataManager;
    private Database database;

    /**
     * Create a Datamanager object.
     *
     * @param context Context of application.
     */
    private DataManager(Context context) {
        DatabaseConfiguration config = new DatabaseConfiguration(context);
        try {
            database = new Database(DB_NAME, config);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Could not create database", e);
        }
    }

    /**
     * Get or create a DataManager instance.
     *
     * @param context Context of application.
     * @return The DataManager instance.
     */
    public static synchronized DataManager getInstance(Context context) {
        if (dataManager == null) {
            dataManager = new DataManager(context);
        }
        return dataManager;
    }

    /**
     * Attempt to get an existing DataManager object.
     *
     * @return The DataManager.
     * @throws IllegalStateException If the DataManager instance is not instantiated.
     */
    public static synchronized DataManager getInstance() {
        if (null == dataManager) {
            throw new IllegalStateException(DataManager.class.getSimpleName() + " is not initialized");
        }
        return dataManager;
    }

    /**
     * Add a model to the database.
     *
     * @param model Model to add to database.
     * @throws CouchbaseLiteException If inable to save document/model.
     */
    public void addModel(Model model) throws CouchbaseLiteException {
        MutableDocument document = new MutableDocument();
        document.setData(model.toMap());
        document.setString(MODEL_TYPE_KEY, model.modelType);
        document.setString(MODEL_ID_KEY, model.getModelId());
        database.save(document);
    }

    /**
     * Retrieves all properties of document that matches the modelId and type. It is then casted
     * into objectType and passed to the DocumentParser @see {@link DocumentParser#toModelCallback(Object)}.
     *
     * @param modelId The id representing the model object. <b>NOT</b> the ID of the document.
     * @param modelType The type name of the model.
     * @param objectType The class type of the object representing the model.
     * @param documentParser Document parser interface used to convert the document to appropriate model type.
     * @throws CouchbaseLiteException Exception thrown when inable to query document.
     */
    public void getModel(String modelId, String modelType, Class objectType, DocumentParser documentParser) throws CouchbaseLiteException {
        Query query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.database(database))
                .where(
                        Expression.property(MODEL_TYPE_KEY).equalTo(Expression.string(modelType))
                                .and(Expression.property(MODEL_ID_KEY).equalTo(Expression.string(modelId)))
                );
        Dictionary objectDictionary = query.execute().next().getDictionary(0);
        //noinspection unchecked
        documentParser.toModelCallback(new ObjectMapper().convertValue(objectDictionary.toMap(), objectType));
    }

    /**
     * Retrieves all properties of document that matches the type. It is then casted
     * into objectType and passed to the DocumentParser @see {@link DocumentParser#toModelCallback(Object)}.
     *
     * @param modelType The type name of the model.
     * @param objectType The class type of the object representing the model.
     * @param documentParser Document parser interface used to convert the document to appropriate model type.
     * @throws CouchbaseLiteException Exception thrown when inable to query document.
     */
    public void getModels(String modelType, Class objectType, DocumentParser documentParser) throws CouchbaseLiteException {
        Query query = QueryBuilder
                .select(SelectResult.all())
                .from(DataSource.database(database))
                .where(Expression.property(MODEL_TYPE_KEY)
                        .equalTo(Expression.string(modelType)));
        ResultSet results = query.execute();
        ObjectMapper objectMapper = new ObjectMapper();
        List models = new ArrayList();
        for (Result result : results) {
            //noinspection unchecked
            models.add(objectMapper.convertValue(result.getDictionary(0).toMap(), objectType));
        }
        //noinspection unchecked
        documentParser.toModelCallback(models);
    }

    /**
     * Represents functionality needed to parse a Document into a Model class.
     *
     * @param <T>
     */
    public interface DocumentParser<T> {

        /**
         * Callback with correct Model type.
         *
         * @param model The model.
         */
        void toModelCallback(T model);
    }
}
