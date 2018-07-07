package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public abstract class BaseModel {

    @JsonIgnore
    public final String modelType;
    @JsonIgnore
    protected String modelId;

    public BaseModel(String modelType) {
        this.modelType = modelType;
    }

    /**
     * Convert this model object into a representative map.
     *
     * @return A map representing this model.
     */
    public abstract Map<String, Object> toMap();

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
