package com.spring.city.controller;

import com.spring.city.model.CityData;
import com.spring.city.model.CityForm;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping
public class CityFormController {

    @GetMapping(value="/springcity", produces = MediaType.TEXT_HTML_VALUE)
    public String formHTML(Model model) {
        model.addAttribute("cityForm", new CityForm());
        return "cityform";
    }

    @PostMapping(value = "/springcity", produces = MediaType.TEXT_HTML_VALUE)
    public String submitHTML(@ModelAttribute CityForm cityForm, Model model) {
        List<CityData> cityResults = new CityComputer().rankCities(cityForm);
        model.addAttribute("cityResults", cityResults);
        return "result";
    }
}