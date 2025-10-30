package com.lcwd.user.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lcwd.user.service.Exceptions.ResourceNotFoundException;
import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HotelService hotelService;
	
	private Log logger=LogFactory.getLog(UserServiceImpl.class);

	@Override
	public User saveUser(User user) {
		//for generating unique userid
		String randomUserId = UUID.randomUUID().toString();
		user.setUserId(randomUserId);
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		//implement rating service call using rest template
		return userRepository.findAll();
	} 

//	@Override
//	public User getUser(String userId) {
//	 User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user with given id is not found on server : "+userId));
//	 //fetch rating of above user from rating services
//	 //http://localhost:8083/ratings/users/56212b2c-669e-4420-8724-b010b7cce1d0
//	 Rating[] ratingsForUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+user.getUserId(), Rating[].class);
//	 List<Rating> ratings = Arrays.stream(ratingsForUser).toList();
//	 logger.info("{}"+ratingsForUser);
//	 List<Rating> ratingList = ratings.stream().map(rating ->{
//		 //api call to hotel service to get the hotel
//		 //http://localhost:8082/hotels/19a936a6-3e2f-451c-90d5-7bb91bf3993c
//		 ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//		 Hotel hotel  = forEntity.getBody();
//		 logger.info("response status code : "+forEntity.getStatusCode());
//		 //set the hotel to rating
//		 rating.setHotel(hotel);
//		 //return the rating 
//		 return rating;
//	 }).collect(Collectors.toList());
//	 user.setRatings(ratingList);
//	 return user;
//
//	}
	@Override
	public User getUser(String userId) {
	 User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user with given id is not found on server : "+userId));
	 //fetch rating of above user from rating services
	 //http://localhost:8083/ratings/users/56212b2c-669e-4420-8724-b010b7cce1d0
	 Rating[] ratingsForUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+user.getUserId(), Rating[].class);
	 List<Rating> ratings = Arrays.stream(ratingsForUser).toList();
	 logger.info("{}"+ratingsForUser);
	 List<Rating> ratingList = ratings.stream().map(rating ->{
		 //api call to hotel service to get the hotel
		 //http://localhost:8082/hotels/19a936a6-3e2f-451c-90d5-7bb91bf3993c
//		 ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
		 Hotel hotel  = hotelService.getHotel(rating.getHotelId());
//		 logger.info("response status code : "+forEntity.getStatusCode());
		 //set the hotel to rating
		 rating.setHotel(hotel);
		 //return the rating 
		 return rating;
	 }).collect(Collectors.toList());
	 user.setRatings(ratingList);
	 return user;

	}

	@Override
	public void deleteUser(String userId) {
		User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user with the given id is not found on the server : "+ userId));
		userRepository.delete(user);
	}

	@Override
	public User updatedUser(String userId, User updatedUser) {
		User existingUser = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user with the given id is not found on the server : "+userId));
		existingUser.setName(updatedUser.getName());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setAbout(updatedUser.getAbout());
		return userRepository.save(existingUser);
	}

}
