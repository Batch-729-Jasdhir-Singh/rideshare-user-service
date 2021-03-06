package com.revature.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.revature.beans.Admin;
import com.revature.repositories.AdminRepository;

@RunWith(SpringRunner.class)
public class AdminServiceImplTest {
	
	@InjectMocks
	private AdminServiceImpl asi;
	
	@Mock
	private AdminRepository ar;
	
	
	@Test
	public void testGettingAdmins() {
		
		List<Admin> admins = new ArrayList<>();
		admins.add(new Admin());
		admins.add(new Admin());
		when(ar.findAll()).thenReturn(admins);
		
		assertEquals(2, asi.getAdmins().size());

	}
	
	@Test
	public void testGettingAdminById() {
		System.out.println("BY ID:");
		Admin expected = new Admin(1, "username");
		Optional<Admin> o = Optional.of(expected);
		when(ar.findById(1)).thenReturn(o);
		Admin actual = asi.getAdminById(1);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreatingAdmin() {
		
		Admin expected = new Admin(2, "otheruser");
		when(ar.save(expected)).thenReturn(expected);
		Admin actual = asi.createAdmin(expected);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdatingAdmin() {
		
		Admin expected = new Admin(1, "username");
		when(ar.save(expected)).thenReturn(expected);
		Admin actual = asi.updateAdmin(expected);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeletingAdmin() {
		
		String expected = "Admin with id: 1 was deleted.";
		when(ar.existsById(1)).thenReturn(true);
		String actual = asi.deleteAdminById(1);
		
		assertEquals(expected, actual);
	}
	
}
