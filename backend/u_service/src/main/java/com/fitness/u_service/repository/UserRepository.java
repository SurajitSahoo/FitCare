package com.fitness.u_service.repository;

import com.fitness.u_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByEmail(String email); //check if any record is present in this email or not

}
