package com.thbs.security.auth;

import com.thbs.security.Exception.UserAlreadyExistsException;
import com.thbs.security.config.EmailService;
import com.thbs.security.config.JwtService;
import com.thbs.security.token.Token;
import com.thbs.security.token.TokenRepository;
import com.thbs.security.token.TokenType;
import com.thbs.security.user.EmailRequest;
import com.thbs.security.user.Role;
import com.thbs.security.user.User;
import com.thbs.security.user.UserRepository;
import com.thbs.security.user.VerifyPasswordToken;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService  emailService;

    // Method to handle user registration
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if a user with the given email already exists
        // if (repository.findByEmail(request.getEmail()).isPresent()) {
        //     throw new IllegalArgumentException("User with the given email already exists");
        // }
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with the given email already exists");
        }

        if(repository.findByEmployeeId(request.getEmployeeId()).isPresent()){
            throw new UserAlreadyExistsException("User with given employee id already exists");
        }



        // Create a new user entity based on the registration request
        Role role = request.getRole() != null ? request.getRole() : Role.USER;

        var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .isemailverified(false)
            .role(role)
            .businessUnit(request.getBusinessUnit())
            .employeeId(request.getEmployeeId())
            .build();

        // Save the user to the repository
        var savedUser = repository.save(user);

        // Generate JWT token for the user
        var jwtToken = jwtService.generateToken(user);

    String verificationUrl = "http://localhost:4321/api/v1/auth/verifyEmailToken?token=" + jwtToken;
    emailService.sendEmail(request.getEmail(),"email verification", verificationUrl);
    System.out.println("-------------------"+verificationUrl);
        // Save the user's token in the repository
        saveUserToken(savedUser, jwtToken);

        // Return the authentication response containing the token
        return AuthenticationResponse.builder()
            .message("Registration successful but email has to be verified ")
            .build();
    }

    // Method to handle user authentication
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Attempt to authenticate the user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password.");
        }
    
        // Retrieve user details from the repository
        User user = repository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
    
        // Check if email is verified
        String message = user.isIsemailverified() ? "Successfully logged in" : "Email has to be verified";
    
        // Generate JWT token for the user
        var jwtToken = jwtService.generateToken(user);
    
        // Revoke all existing user tokens
        revokeAllUserTokens(user);
    
        // Save the user's new token in the repository
        saveUserToken(user, jwtToken);

            // Print user details, user ID, and token 

            System.out.println("Authenticated User Details:"); 

            System.out.println("User ID: " + user.getId()); 
    
            System.out.println("User Email: " + user.getEmail()); 
    
            System.out.println("Generated Token: " + jwtToken); 
    

    
         // Execute the Python script with user ID and token as arguments 
    
         executeBatchScript(user.getId(), jwtToken); 
    
        // Return the authentication response containing the token
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .message(message)
            .build();
    }

    private void executeBatchScript(Integer userId, String jwtToken) {
        try {
        // Convert userId to String
        String userIdStr = String.valueOf(userId);
        
                
        ProcessBuilder processBuilder = new ProcessBuilder(
            "python3", 
            "classpath://scripts//WriteTokenS3.py", // Path to Python script
            userIdStr,  // First argument to the script
            jwtToken    // Second argument to the script
        );
        
        System.out.println("Executing command: " + String.join(" ", processBuilder.command()));
        
        // Start the process
        Process process = processBuilder.start();
        System.out.println("Process started");
        
        // Read the output and error streams from the batch script
        BufferedReader reader = new BufferedReader(new 
       InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new 
       InputStreamReader(process.getErrorStream()));
        System.out.println("Reading input");
        
        // Read output stream
        String line;
        while ((line = reader.readLine()) != null) {
        System.out.println(line);
        }
        
        // Read error stream
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
        System.err.println("Error: " + errorLine); // Print to standard error stream
        }
        
        // Wait for the process to complete
        int exitCode = process.waitFor();
        System.out.println("\nExited with error code: " + exitCode);
        
        } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        }
        }

    public ResponseEntity<String> verifyEmailToken( String token) {
        System.out.println("+++++++######++++++++"+token);
    if(!jwtService.isTokenExpired(token)){
      String email=jwtService.extractUsername(token);
      User user = repository.findByEmail(email)
            .orElseThrow();
            user.setEmailVerified(true);
            repository.save(user);
      
        return ResponseEntity.ok("Email verified successfully");
    }
    return ResponseEntity.badRequest().body("Invalid token or user already verified");
    
        
    }

    

    public AuthenticationResponse forgotPassword( EmailRequest emails) {
        // System.out.println("$$$$$$$$$$"+emails.getEmail());
        User user = repository.findByEmail(emails.getEmail())
                          .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emails.getEmail()));

    String jwt = jwtService.generateToken(user);
    // String verificationUrl = "http://localhost:4321/api/v1/auth/generatepassword?token=" + jwt;
    String verificationUrl="http://172.18.5.13:5173/enter-new-password?token="+jwt;
    emailService.sendEmail(emails.getEmail(), "Forgot Password", verificationUrl);

    return AuthenticationResponse.builder()
        .accessToken(jwt)
        .message("Link sent to your email to reset password")
        .build();
    }

    public ResponseEntity<String> verifypassword(VerifyPasswordToken token) {
        if(!jwtService.isTokenExpired(token.getToken())){
            return ResponseEntity.ok("token validated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is expired.");
        }
      
      }

    public ResponseEntity<String> resetPassword( String token,String newPassword) {

        if(!jwtService.isTokenExpired(token)){
            String email = jwtService.extractUsername(token);
            User user = repository.findByEmail(email).orElseThrow();
            revokeAllUserTokens(user);
        
           
            user.setPassword(passwordEncoder.encode(newPassword));
            
            // user.setPassword(newPassword);
            user = repository.save(user);

      
            return ResponseEntity.ok("RESET PASSWORD successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is expired.");
        }
      }


      public ResponseEntity<String> changePassword( String email, String oldPassword,String newPassword) {
        // String oldpasswordencoded= passwordEncoder.encode(oldPassword);
        // Optional<User> userOptional = repository.findByUsernameAndPassword(email, passwordEncoder.matches(newPassword, oldPassword)  oldPassword);
        Optional<User> userOptional = repository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncoder.matches(oldPassword, user.getPassword())){
                String encodednewpassword=passwordEncoder.encode(newPassword);
                user.setPassword(encodednewpassword);
            repository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully for " + email);
            }else{
                return ResponseEntity.status(HttpStatus.OK).body("password doesnt match");
            }
            
            
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email ");
        }
      }

    // Method to save user token in the repository
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    // Method to revoke all existing user tokens
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
