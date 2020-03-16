
package com.spring.city.model.teleport.scores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.*;
import com.spring.city.model.teleport.Category;
import com.spring.city.model.teleport.Links;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "_links",
    "categories",
    "summary",
    "teleport_city_score"
})
public class LifeData {

    @JsonProperty("_links")
    private Links links;
    @JsonProperty("categories")
    private List<Category> categories = null;
    @JsonProperty("summary")
    private String summary;
    @JsonProperty("teleport_city_score")
    private Double teleportCityScore;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_links")
    public Links getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }

    @JsonProperty("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty("teleport_city_score")
    public Double getTeleportCityScore() {
        return teleportCityScore;
    }

    @JsonProperty("teleport_city_score")
    public void setTeleportCityScore(Double teleportCityScore) {
        this.teleportCityScore = teleportCityScore;
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
        return "LifeData{" +
                "links=" + links +
                ", categories=" + categories +
                ", summary='" + summary + '\'' +
                ", teleportCityScore=" + teleportCityScore +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
