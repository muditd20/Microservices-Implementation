package com.lcwd.rating.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lcwd.rating.entites.Rating;

public interface RatingRepository extends MongoRepository<Rating, String>{

	//custom finder methods
	 List<Rating> findByUserId(String userId);
	 List<Rating> findByHotelId(String HotelId);
}
