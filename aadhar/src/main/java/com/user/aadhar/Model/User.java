package com.user.aadhar.Model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="user_details")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String aadharNumber;

	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "aadhar_response_id", referencedColumnName = "id")
	
	AadharResponse response;
	
    @Transient
	private String password;
	
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public AadharResponse getResponse() {
		return response;
	}

	public void setResponse(AadharResponse response) {
		this.response = response;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", aadharNumber=" + aadharNumber + ", response=" + response
				+ ", password=" + password + "]";
	}
	
	
}
