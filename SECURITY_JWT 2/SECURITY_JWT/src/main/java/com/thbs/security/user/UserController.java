package com.thbs.security.user;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thbs.security.DTO.UserDetailsDTO;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users") // Base URL path for the UserController
@RequiredArgsConstructor // Lombok annotation to generate a constructor with required arguments
@CrossOrigin(origins = {"172.18.4.192", "172.18.5.13:5173"})
public class UserController {

    
 // Instance of UserService injected via constructor

    @Autowired
    private UserRepository userRepository;
    
    // Instance of UserService injected via constructor

    // Endpoint to handle PATCH requests for changing user passwords
   
    @GetMapping("/userdetails")
    public ResponseEntity<List<UserDetailsDTO>> getFilteredUsers() {
        List<User> users = userRepository.findAllByRole(Role.USER);
        List<UserDetailsDTO> userDTOs = users.stream()
            .map(user -> new UserDetailsDTO(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getEmployeeId(),
                user.getBusinessUnit(),
                user.getRole()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

}
