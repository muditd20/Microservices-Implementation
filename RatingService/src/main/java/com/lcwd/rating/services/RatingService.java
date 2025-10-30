package com.lcwd.rating.services;

import java.util.List;

import com.lcwd.rating.entites.Rating;

public interface RatingService {
	
	//create
	Rating create(Rating rating);
	
	//get all rating
	List<Rating> getAllRatings();

	//get all by user id
	List<Rating> getRatingByUserId(String userId);
	
	//get all by hotel id
	List<Rating> getRatingByHotelId(String hotelId);
}
