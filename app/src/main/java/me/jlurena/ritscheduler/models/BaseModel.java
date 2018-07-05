package me.jlurena.ritscheduler.models;

import java.util.Map;

public interface BaseModel {

    String getType();
    Map toMap();
}
