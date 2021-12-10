package com.recsys.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.recsys.model.RecommendedMovie;

public class RecommendationRepo {
	
	private static Map<Integer,RecommendedMovie> recommendationsDB = new HashMap<>();

	public void saveAll(List<RecommendedMovie> movies) {
		Map<Integer,RecommendedMovie> recMap = movies.stream()
				.collect(Collectors.toMap(RecommendedMovie::getUserId, Function.identity()));
		recommendationsDB.putAll(recMap);
	}
	
	public List<RecommendedMovie> findAll() {
		return new ArrayList<>(recommendationsDB.values());
	}
	
	public int getNextMovie() {
		return recommendationsDB.keySet()
				.stream()
				.mapToInt(value -> value)
				.max()
				.orElse(0) + 1;
	}
}
