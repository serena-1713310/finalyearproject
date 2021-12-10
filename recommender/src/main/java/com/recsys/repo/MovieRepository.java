package com.recsys.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recsys.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Integer>{
	
	Movie findByMovieId(int movieId);
	
	List<Movie> findAll();
	
	List<Movie> findByTitle(String title);
	
	void deleteById(int movieId);
	
	List<Movie> searchByTitle(String title);
	
}
