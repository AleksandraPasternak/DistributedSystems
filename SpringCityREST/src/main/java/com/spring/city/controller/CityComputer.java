package com.spring.city.controller;

import com.spring.city.model.CityData;
import com.spring.city.model.CityForm;
import com.spring.city.model.teleport.Category;
import com.spring.city.model.teleport.image.CityImage;
import com.spring.city.model.teleport.scores.LifeData;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CityComputer {

    public LifeData getLifeData(String cityName) {
        String cityNameParsed = cityName.toLowerCase().replace(" ", "-");
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate template = builder.build();
        LifeData lifeData = template.getForObject(
                "https://api.teleport.org/api/urban_areas/slug:"+cityNameParsed+"/scores/", LifeData.class);
        return lifeData;
    }

    public String getCityImage(String cityName) {
        String cityNameParsed = cityName.toLowerCase().replace(" ", "-");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        final String url = "https://api.teleport.org/api/urban_areas/slug:"+cityNameParsed+"/images/";
        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate template = builder.build();
        ResponseEntity<CityImage> exchange = template.exchange(URI.create(url), HttpMethod.GET, entity, CityImage.class);
        return exchange.getBody().getPhotos().get(0).getImage().getWeb();
    }

    public List<CityData> computeCities(List<CityData> cities, List<String> factors){
        cities.stream().forEach(city -> {
            double result = 0;
            for (int i = 0; i < factors.size(); i++) {
                String currentFactor = factors.get(i);
                Category category = city.getCategories().stream()
                        .filter(cat -> cat.getName().equals(currentFactor))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
                result += (i == 0) ? category.getScoreOutOf10() : category.getScoreOutOf10() * (1 - i / 10.0);
            }
            city.setResult(result);
        });
        Collections.sort(cities, Collections.reverseOrder());
        return cities;
    }

    public List<CityData> rankCities(CityForm cityForm){
        List<CityData> citiesData = new ArrayList<>();
        citiesData.add(new CityData(cityForm.getFirstCity(),
                getLifeData(cityForm.getFirstCity()).getCategories(),
                getCityImage(cityForm.getFirstCity()), 0.0));
        citiesData.add(new CityData(cityForm.getSecondCity(),
                getLifeData(cityForm.getSecondCity()).getCategories(),
                getCityImage(cityForm.getSecondCity()),0.0));
        citiesData.add(new CityData(cityForm.getThirdCity(),
                getLifeData(cityForm.getThirdCity()).getCategories(),
                getCityImage(cityForm.getThirdCity()),0.0));
        List<String> factors = new ArrayList<>();
        factors.add(cityForm.getFirstFactor());
        factors.add(cityForm.getSecondFactor());
        factors.add(cityForm.getThirdFactor());
        return computeCities(citiesData, factors);
    }

}
