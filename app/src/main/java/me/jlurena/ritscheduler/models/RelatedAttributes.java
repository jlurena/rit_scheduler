package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedAttributes {

    public static final String type = "relatedAttributes";
    @JsonProperty("ACCS")
    private String[] accs;

    public String[] getAccs() {
        return accs;
    }

    public void setAccs(String[] accs) {
        this.accs = accs;
    }
}
