package com.user.aadhar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.aadhar.Model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long>{

}
