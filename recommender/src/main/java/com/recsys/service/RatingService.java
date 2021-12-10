package com.recsys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recsys.model.UserRating;
import com.recsys.repo.RatingRepository;

@Service
public class RatingService {

	@Autowired
	private RatingRepository repo;
	
	public UserRating get(int id) {
		return repo.findById(id);
	}
	
	public List<UserRating> getAll(){
		return repo.findAll();
	}
	
	public UserRating getByUserIdMovieId(int userId,int movieId) {
		return repo.findByUserIdAndMovieId(userId, movieId);
	}

	public UserRating addRating(int userId,int movieId,double rating) {
		UserRating userRating = repo.findByUserIdAndMovieId(userId, movieId);
		if(userRating != null) {
			userRating = updateRating(userRating.getId(), rating);
		}
		userRating.setTimestamp(System.currentTimeMillis());
		userRating = repo.save(userRating);
		return userRating;
	}
	
	public UserRating updateRating(int id, double rating) {
		UserRating uRating = repo.findById(id);
		uRating.setRating(rating);
		repo.save(uRating);
		return uRating;
	}
	
}
