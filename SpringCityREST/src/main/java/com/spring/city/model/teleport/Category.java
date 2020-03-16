
package com.spring.city.model.teleport;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "color",
    "name",
    "score_out_of_10"
})
public class Category {

    @JsonProperty("color")
    private String color;
    @JsonProperty("name")
    private String name;
    @JsonProperty("score_out_of_10")
    private Double scoreOutOf10;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("color")
    public String getColor() {
        return color;
    }

    @JsonProperty("color")
    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("score_out_of_10")
    public Double getScoreOutOf10() {
        return scoreOutOf10;
    }

    @JsonProperty("score_out_of_10")
    public void setScoreOutOf10(Double scoreOutOf10) {
        this.scoreOutOf10 = scoreOutOf10;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "Category{" +
                "color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", scoreOutOf10=" + scoreOutOf10 +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
