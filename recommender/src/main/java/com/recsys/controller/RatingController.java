package com.recsys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.recsys.model.UserRating;
import com.recsys.service.ALSModelService;
import com.recsys.service.RatingService;

@Controller
@RequestMapping("/rating")
public class RatingController {

	@Autowired
	private RatingService rService;
	
	@Autowired
	private ALSModelService alsService;
	
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String showRatingForm(Model model) {
		model.addAttribute("rating",new UserRating());
		return "rating/new_user";
	}
	
	@RequestMapping(value="/all",method=RequestMethod.GET)
	public String showAllRatings(Model model) {
		model.addAttribute("ratingsList",rService.getAll().subList(0, 100));
		return "rating/all_ratings";
	}
	
	@RequestMapping(value="/train", method = RequestMethod.POST)
	public String trainModel() {
		alsService.trainModel();
		return "recommender/train_success";
	}
	
	@RequestMapping(value="/new",method = RequestMethod.POST)
	public String addRating(@ModelAttribute UserRating rating) {
		UserRating r = rService.addRating(rating.getUserId(), rating.getMovieId(), rating.getRating());
		System.out.println("inserted rating: "+r.toString());
		return "rating/all_ratings";
	}
}
