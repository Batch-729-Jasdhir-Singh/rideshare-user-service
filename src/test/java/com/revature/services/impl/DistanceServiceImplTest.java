package com.revature.services.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;

import com.google.maps.errors.ApiException;
import com.revature.beans.Batch;
import com.revature.beans.User;
import com.revature.repositories.UserRepository;
import com.revature.services.UserService;

@RunWith(SpringRunner.class)
public class DistanceServiceImplTest {
	
	@InjectMocks
	private DistanceServiceImpl dsi;
	
	@Mock
	private UserService us;
	
	private static User testRider;
	private static User driverOne;
	private static User driverTwo;
	private static User driverThree;
	private static List<User> driverList = new ArrayList<User>();
	
	
	@BeforeClass
	public static void setupUsers() {
		testRider = new User(1, "TestUser", new Batch(0, "Reston"), "Fred", "Jones", "FJTest@gmail.com", "1234561234", false, true, false,
				"1400 Dulles Plaza", "Herndon", "11112", "Virginia", "11730 Plaza America Dr.", "Reston", "11111", "Virginia");
		
		driverOne = new User(2, "TestDriver1", new Batch(0, "Reston"), "Ted", "Lones", "TLTest@gmail.com", "1234561234", true, true, true,
				"1202 Springtide Place", "Herndon", "11112", "Virginia", "11730 Plaza America Dr.", "Reston", "11111", "Virginia");
		driverTwo = new User(3, "TestDriver2", new Batch(0, "Reston"), "Ned", "Bones", "NBTest@gmail.com", "1234561234", true, true, true,
				"46025 Bayswater Terrace", "Herndon", "11112", "Virginia", "11730 Plaza America Dr.", "Reston", "11111", "Virginia");
		driverThree = new User(4, "TestDriver3", new Batch(0, "Reston"), "Jed", "Hones", "JHTest@gmail.com", "1234561234", true, true, true,
				"738 Cordell Way", "Herndon", "11112", "Virginia", "11730 Plaza America Dr.", "Reston", "11111", "Virginia");
		
		driverList.add(driverOne);
		driverList.add(driverTwo);
		driverList.add(driverThree);
		
	}

	//initDestinations
	@Test
	public void testInitDestinations() {
		String[] destTest = dsi.initDestinations(testRider);

		assertTrue(destTest[0].equals("1400 Dulles Plaza, Herndon, Virginia"));
		assertTrue(destTest[1].equals("11730 Plaza America Dr., Reston, Virginia"));
	}
	
	//initOrigins
	@Test
	public void testInitOrigins() {
		String[] oriTest = dsi.initOrigins(testRider, driverList);
		assertTrue(oriTest[0].equals("1400 Dulles Plaza, Herndon, Virginia"));
		assertTrue(oriTest[1].equals("1202 Springtide Place, Herndon, Virginia"));
		assertTrue(oriTest[2].equals("46025 Bayswater Terrace, Herndon, Virginia"));
		assertTrue(oriTest[3].equals("738 Cordell Way, Herndon, Virginia"));
	}
	
	//getAddressFromUsers
	@Test
	public void testGetAddressFromUsers() {
		List<String> addsTest = dsi.getAddressFromUsers(driverList);
		
		assertTrue(addsTest.get(0).equals("1202 Springtide Place, Herndon, Virginia"));
		assertTrue(addsTest.get(1).equals("46025 Bayswater Terrace, Herndon, Virginia"));
		assertTrue(addsTest.get(2).equals("738 Cordell Way, Herndon, Virginia"));
		
	}
	
	//Tests that all distances calculated should return positive
	@Test
	public void testCalculateDistance() {
		Mockito.when(us.getActiveDrivers()).thenReturn(driverList);
		String[] origins = dsi.initOrigins(testRider, driverList);
		String[] destinations = dsi.initDestinations(testRider);
		List<Double> calcDistances = new ArrayList<Double>();
		try {
			calcDistances = dsi.calculateDriverDistances(origins, destinations);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Double d : calcDistances) {
			assertTrue(d >= 0);
		}		
	}
	
	@Test
	public void testRecommendDrivers() {
		Mockito.when(us.getActiveDrivers()).thenReturn(driverList);
		List<User> recDrivers = dsi.recommendDrivers(testRider, 2);
		
	}
	
	@Test
	public void getBatchActiveDrivers() {
		List<User> testUList = new ArrayList<User>();
		testUList.add(testRider);
		testUList.add(driverOne);
		testUList.add(driverTwo);
		
		//Testing a user from a different batch
		User testRider2 = new User(4, "TestUserBat", new Batch(1, "Morgantown"), "Fred2", "Jones2", "FJ2Test@gmail.com", "1234561234", false, true, false,
				"1400 Dulles Plaza", "Herndon", "11112", "Virginia", "11730 Plaza America Dr.", "Reston", "11111", "Virginia");
		testUList.add(testRider2);
		
		Mockito.when(us.getActiveDrivers()).thenReturn(testUList);
		List<User> updatedList = dsi.getBatchActiveDrivers(testRider);
		
		assertTrue(updatedList.contains(driverOne));
		assertTrue(updatedList.contains(driverTwo));
		assertFalse(updatedList.contains(testRider));
		assertFalse(updatedList.contains(testRider2));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
