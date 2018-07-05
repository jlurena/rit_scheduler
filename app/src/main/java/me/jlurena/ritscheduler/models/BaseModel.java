package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.UUID;

public interface BaseModel {

    @JsonIgnore
    /**
     * Type name of model.
     * @return The type name.
     */
    String getType();

    @JsonIgnore
    /**
     * Convert model object into a map.
     * @return A map representing this model.
     */
    Map<String, Object> toMap();

    @JsonIgnore
    /**
     * Get the id of this model.
     * @return The id.
     */
    String getId();
}
