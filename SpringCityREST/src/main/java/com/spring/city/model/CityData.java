package com.spring.city.model;

import com.spring.city.model.teleport.Category;

import java.util.List;

public class CityData implements Comparable<CityData>{

    private String cityName;
    private List<Category> categories;
    private String photoURL;
    private Double result;

    public CityData(String cityName, List<Category> categories, String photoURL, Double result) {
        this.cityName = cityName;
        this.categories = categories;
        this.photoURL = photoURL;
        this.result = result;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    @Override
    public int compareTo(CityData o) {
        return this.getResult().compareTo(o.getResult());
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
