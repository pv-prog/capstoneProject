package com.ccms.service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

@Document("Customer")
public class Customer {

	@Id
	private String id;
	@NotNull
	private String username;
	private String password;
	private Name name;
	private String dob;
	private String sex;
	
	@Email
	private String email;
	private int customerId;
	private Address address;
	private boolean active;
	private Date createdAt;

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class Name {

		private String first;
		private String last;

	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class Address {

		private String street;
		private String city;
		private String state;
		private int zip;
		private String country;
	}

}
