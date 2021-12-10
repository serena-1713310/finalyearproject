package com.recsys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.recsys.model.Movie;
import com.recsys.service.MovieService;

@Controller
@RequestMapping("/movies")
public class MovieController {

	@Autowired 
	private MovieService service;
	
	@GetMapping("/all")
	public String showAllMovies(Model model) {
		List<Movie> movies = service.getAll();
		System.out.println("loading: "+movies.size()+" movies");
		model.addAttribute("moviesList",movies);
		return "movies/allMovies";
	}
	
	@GetMapping("/search")
	public String searchMovies(@Param("keyword") String keyword,
			Model model) {
		List<Movie> moviesResult = service.search(keyword);
		model.addAttribute("result",moviesResult);
		model.addAttribute("keyword",keyword);
		
		return "movies/search_result";
	}
	
	@RequestMapping(value="/edit/{movieId}")
	public ModelAndView showEditMoviePage(@PathVariable(name="movieId") int id) {
		ModelAndView mav = new ModelAndView("new");
		Movie movie = service.get(id);
		mav.addObject("movie",movie);
		return mav;
	}
}
