package com.example.kafkaredis.repository;

import com.example.kafkaredis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


}