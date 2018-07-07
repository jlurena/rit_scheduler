package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * Represents the base properties of all core models.
 */
public abstract class Model {

    /**
     * String representing this type of model.
     */
    @JsonIgnore
    public final String modelType;
    @JsonIgnore
    private String modelId;

    Model(String modelType) {
        this.modelType = modelType;
    }

    /**
     * Convert this model object into a representative map.
     *
     * @return A map representing this model.
     */
    public abstract Map<String, Object> toMap();

    /**
     * Get the modelId, a unique ID representing this type of model.
     *
     * @return The modelId.
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * Set the modelId, an ID representing this Model type.
     *
     * @param modelId Unique String ID representing this model type.
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
