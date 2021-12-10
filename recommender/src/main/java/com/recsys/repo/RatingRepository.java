package com.recsys.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recsys.model.UserRating;

@Repository
public interface RatingRepository extends JpaRepository<UserRating,Integer>{

	UserRating findById(int id);
	
	UserRating findByUserIdAndMovieId(int userId,int movieId);
	
	List<UserRating> findAll();
	
	List<UserRating> findByUserId(int userId);
	
	List<UserRating> findByMovieId(int movieId);
	
}
