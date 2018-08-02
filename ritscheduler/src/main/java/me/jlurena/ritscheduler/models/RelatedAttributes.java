package me.jlurena.ritscheduler.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedAttributes {

    @JsonProperty("ACCS")
    private String[] accs;

    public String[] getAccs() {
        return accs;
    }

    public void setAccs(String[] accs) {
        this.accs = accs;
    }
}
