package com.recsys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.recsys.forms.RecommendFormData;
import com.recsys.model.Movie;
import com.recsys.model.Recommendations;
import com.recsys.model.RecommendedMovie;
import com.recsys.service.RecommenderService;

@Controller
@RequestMapping("/recsys")
public class RecommenderController {

	@Autowired
	private RecommenderService recService;

	public RecommenderController(RecommenderService service) {
		this.recService = service;
	}
	
	@RequestMapping(value="/all", method = RequestMethod.GET)
	public String showRecommenderForm(Model model) {
		RecommendFormData form = new RecommendFormData();
		model.addAttribute("form", form);
		return "recommender/recommendation_form";
	}

	@RequestMapping(value="/recommend", method = RequestMethod.POST)
	public String getAllRecommendations(Model model) {
		Recommendations recs = new Recommendations(); 
		List<RecommendedMovie> recList = recService.getRecommendationList();
		recs.getRecList().addAll(recList);
		
		if(recList != null) {
			model.addAttribute("recList", recs);
		} else {
			model.addAttribute("recList", new Recommendations());
		}
		return "/recys/user";
		
	}
	
	@RequestMapping(value="/user",method = RequestMethod.GET)
	public String getUserRecommendations(@ModelAttribute RecommendFormData form,
			Model model) {
		
		int user = form.getUserId();
		List<Movie> recList = recService.getRecommendationForUser(user);
		model.addAttribute("recList",recList);
		return "recommender/myrecs";
	}
	
}
