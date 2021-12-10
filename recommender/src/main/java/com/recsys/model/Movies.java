package com.recsys.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Movies {

	private List<Movie> movies;
	
	@XmlElement
	public List<Movie> getMovieList(){
		if(movies == null) {
			movies = new ArrayList<>();
		}
		return movies;
	}
	
	public int size(){
		return movies.size();
	}
}
