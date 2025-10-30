package com.lcwd.user.service.Controller;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.services.UserService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private Log logger=LogFactory.getLog(UserController.class);
	
	//create user
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user)
	{
		User user1 = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(user1);
	} 
	
	int retryCount=1;
	//get single user
	@GetMapping("/{userId}")
//	@CircuitBreaker(name = "ratingHotelBreaker",fallbackMethod = "ratingHotelFallback")
//	@Retry(name = "ratingHotelService",fallbackMethod = "ratingHotelFallback")
	@RateLimiter(name = "userRateLimiter",fallbackMethod = "ratingHotelFallback")
	public ResponseEntity<User> getSingleUser( @PathVariable String userId){
		logger.info("Retry count: {}"+retryCount);
		retryCount++;
		User user = userService.getUser(userId);
		return ResponseEntity.ok(user);
	}
	
	//created fallback method for circuit breaker
	public ResponseEntity<User> ratingHotelFallback(String userId,Exception ex)
	{
		logger.info("Fallback is executed because service is down" + ex.getMessage());
		User user = User.builder().email("dummy@gmail.com").name("Dummy").about("this user is created dummy because some services is down").userId("45232").build();
		return new ResponseEntity<>(user,HttpStatus.OK);
	}
	
	//get all user
	@GetMapping
	public ResponseEntity<List<User>> getAllUser()
	{
		List<User> allUser = userService.getAllUser();
		return ResponseEntity.ok(allUser);
	}
	
	//delete single user
	@DeleteMapping("/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable String userId)
	{
		userService.deleteUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body(" User with ID " + userId + " has been deleted successfully");
	}
	
	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUser(@PathVariable String userId,@RequestBody User user)
	{
	User updatedUser = userService.updatedUser(userId, user);	
	return ResponseEntity.ok(updatedUser);
	}
	
	
}
