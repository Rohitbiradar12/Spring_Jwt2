package com.thbs.security.user;

import java.util.List;
import java.util.Optional; // Importing the Optional class from the java.util package
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository interface for handling User entity persistence operations
public interface UserRepository extends JpaRepository<User, Integer> {

  // Method to find a user by their email address
  // The return type is Optional<User>, which means it may or may not contain a User object
  Optional<User> findByEmail(String email);

  List<User> findAllByRole(Role role);

  Optional<User> findByEmployeeId(Integer employeeId);

}
