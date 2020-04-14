package com.revature.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.maps.errors.ApiException;
import com.revature.beans.Batch;
import com.revature.beans.User;
import com.revature.services.BatchService;
import com.revature.services.DistanceService;
import com.revature.services.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * UserController takes care of handling our requests to /users. It provides
 * methods that can perform tasks like all users, user by role (true or false),
 * user by username, user by role and location, add user, update user and delete
 * user by id.
 * 
 * @author Adonis Cabreja
 *
 */

@RestController
@RequestMapping("/users")
@CrossOrigin
@Api(tags = { "User" })
public class UserController {

	@Autowired
	private UserService us;

	@Autowired
	private BatchService bs;

	@Autowired
	private DistanceService ds;

	/**
	 * HTTP GET method (/users)
	 * 
	 * @param isDriver represents if the user is a driver or rider.
	 * @param username represents the user's username.
	 * @param location represents the batch's location.
	 * @return A list of all the users, users by is-driver, user by username and
	 *         users by is-driver and location.
	 */

	/*
	 * @ApiOperation(value="Returns user drivers", tags= {"User"})
	 * 
	 * @GetMapping public List<User> getActiveDrivers() { return
	 * us.getActiveDrivers(); }
	 */

	@ApiOperation(value = "Returns user drivers", tags = { "User" })
	@GetMapping("/driver/{address}")
	public List<User> getTopFiveDrivers(@PathVariable("address") String address)
			throws ApiException, InterruptedException, IOException {
		// List<User> aps = new ArrayList<User>();
		System.out.println(address);
		List<String> destinationList = new ArrayList<String>();
		String[] origins = { address };
//		
		Map<String, User> topfive = new HashMap<String, User>();
//		
		for (User d : us.getActiveDrivers()) {
//			
			String add = d.gethAddress();
			String city = d.gethCity();
			String state = d.gethState();

			String fullAdd = add + ", " + city + ", " + state;

			destinationList.add(fullAdd);
//			
			topfive.put(fullAdd, d);
//						
		}
//		
//		System.out.println(destinationList);
//		
		String[] destinations = new String[destinationList.size()];
////		
		destinations = destinationList.toArray(destinations);
//		
		return ds.distanceMatrix(origins, destinations);
//		
//		
		// return ds.distanceMatrix();

	}

	/**
	 * HTTP GET method (/users)
	 * 
	 * @param isDriver represents if the user is a driver or rider.
	 * @param username represents the user's username.
	 * @param location represents the batch's location.
	 * @return A list of all the users, users by is-driver, user by username and
	 *         users by is-driver and location.
	 */

	@ApiOperation(value = "Returns all users", tags = {
			"User" }, notes = "Can also filter by is-driver, location and username")
	@GetMapping
	public List<User> getUsers(@RequestParam(name = "is-driver", required = false) Boolean isDriver,
			@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "location", required = false) String location) {

		if (isDriver != null && location != null) {
			return us.getUserByRoleAndLocation(isDriver.booleanValue(), location);
		} else if (isDriver != null) {
			return us.getUserByRole(isDriver.booleanValue());
		} else if (username != null) {
			return us.getUserByUsername(username);
		}

		return us.getUsers();
	}

	/**
	 * HTTP GET (users/{id})
	 * 
	 * @param id represents the user's id.
	 * @return A user that matches the id.
	 */

	@ApiOperation(value = "Returns user by id", tags = { "User" })
	@GetMapping("/{id}")
	public User getUserById(@PathVariable("id") int id) {

		return us.getUserById(id);
	}

	/**
	 * HTTP POST method (/users)
	 * 
	 * @param user represents the new User object being sent.
	 * @param BindingResult holds the result of attempting to bind the JSON object in the body with the User object.
	 * 
	 *         Sends custom error messages when incorrect input is used
	 * 
	 *         TODO: REFACTOR so that errors are added to the result.
	 */

	@ApiOperation(value = "Adds a new user", tags = { "User" })
	@PostMapping
	public Map<String, Set<String>> addUser(@Valid @RequestBody User user, BindingResult result) {

		System.out.println(user.isDriver());
		Map<String, Set<String>> errors = new HashMap<>();
		String field, errorMessage;
		
		for (FieldError fieldError : result.getFieldErrors()) {
			field = fieldError.getField();
			errorMessage = getErrorMessage(fieldError);
			errors.computeIfAbsent(field, key -> new HashSet<>()).add(errorMessage);
		}
		if (errors.isEmpty()) {
			Batch batch;
			try {
				batch = bs.getBatchByNumber(user.getBatch().getBatchNumber());
			} catch( EntityNotFoundException e) {
				batch = bs.addBatch(user.getBatch());
			}
			user.setBatch(batch);
			us.addUser(user);
		}
		return errors;
	}

	/**
	 * getErrorMeassage
	 * Takes a field error and translates it to a human readable error message.
	 * 
	 * @param FieldError, the error to be parsed
	 * 
	 * @return A string that contains a human error message.
	 * 
	 */
	protected String getErrorMessage(FieldError fieldError) {
		String code = fieldError.getCode();
		String field = fieldError.getField();
		if (code.equals("NotBlank") || code.equals("NotNull")) {

			switch (field) {
			case "userName":
				return ("Username field required");
			case "firstName":
				return ("First name field required");
			case "lastName":
				return ("Last name field required");
			case "wAddress":
				return ("Work address field required");
			case "wState":
			case "hState":
				return ("State field required");
			case "phoneNumber":
				return ("Phone number field required");
			case "hAddress":
				return ("Home address field required");
			case "hZip":
			case "wZip":
				return ("Zip code field required");
			case "hCity":
			case "wCity":
				return ("City field required");
			default:
				return (field + " required");
			}
		}
		// username custom error message
		else if (code.equals("Size") && field.equals("userName")) {
			return ("Username must be between 3 and 12 characters in length");
		} else if (code.equals("Pattern") && field.equals("userName")) {
			return ("Username may not have any illegal characters such as $@-");
		} else if (code.equals("Valid") && field.equals("userName")) {
			return ("Invalid username");
		}
		// first name custom error message
		else if (code.equals("Size") && field.equals("firstName")) {
			return ("First name cannot be more than 30 characters in length");
		} else if (code.equals("Pattern") && field.equals("firstName")) {
			return ("First name allows only 1 space or hyphen and no illegal characters");
		} else if (code.equals("Valid") && field.equals("firstName")) {
			return ("Invalid first name");
		}
		// last name custom error message
		else if (code.equals("Size") && field.equals("lastName")) {
			return ("Last name cannot be more than 30 characters in length");
		} else if (code.equals("Pattern") && field.equals("lastName")) {
			return ("Last name allows only 1 space or hyphen and no illegal characters");
		} else if (code.equals("Valid") && field.equals("lastName")) {
			return ("Invalid last name");
		}
		// email custom error messages
		else if (code.equals("Email") && field.equals("email")) {
			return ("Invalid Email");
		} else if (code.equals("Pattern") && field.equals("email")) {
			return ("Invalid Email");
		}
		// phone number custom error messages
		else if (code.equals("Pattern") && field.equals("phoneNumber")) {
			return ("Invalid Phone Number");
		}
		return (fieldError.toString());

	}

	/**
	 * HTTP PUT method (/users)
	 * 
	 * @param user represents the updated User object being sent.
	 * @return The newly updated object.
	 */

	@ApiOperation(value = "Updates user by id", tags = { "User" })
	@PutMapping("/{id}")
	public User updateUser(@Valid @RequestBody User user, BindingResult result) {
		return us.updateUser(user);
	}

	/**
	 * HTTP DELETE method (/users)
	 * 
	 * @param id represents the user's id.
	 * @return A string that says which user was deleted.
	 */

	@ApiOperation(value = "Deletes user by id", tags = { "User" })
	@DeleteMapping("/{id}")
	public String deleteUserById(@PathVariable("id") int id) {

		return us.deleteUserById(id);
	}

}
