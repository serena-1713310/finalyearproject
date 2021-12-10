package com.recsys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recsys.model.Movie;
import com.recsys.repo.MovieRepository;

@Service
public class MovieService {

	@Autowired
	private MovieRepository repo;

	public Movie get(int id) {
		return repo.findByMovieId(id);
	}
		
	public List<Movie> getAll() {
		return repo.findAll();
	}

	private Movie parseMovieLine(String line) {
		String[] col = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		int movieId = Integer.parseInt(col[0]);
		String title = col[1];
		String genre = col[2];

		return new Movie(movieId,title,genre);
	}

	public List<Movie> findByName(String title) {
		return repo.findByTitle("%"+title+"%");
	}

	public void delete(int id) {
		repo.deleteById(id);
	}
	
	public List<Movie> search(String title) {
		return repo.searchByTitle(title);
	}

}
