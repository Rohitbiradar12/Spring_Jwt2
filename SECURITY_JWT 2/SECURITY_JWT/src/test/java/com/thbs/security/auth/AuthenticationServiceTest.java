// package com.thbs.security.auth;


// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import com.thbs.security.user.EmailRequest;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// public class AuthenticationServiceTest {

//     @Autowired
//     private AuthenticationService authenticationService;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Test
// public void testRegister() {
//     RegisterRequest request = new RegisterRequest();
//     request.setFirstname("John");
//     request.setLastname("Doe");
//     request.setEmail("john.doe@example.com");
//     request.setPassword("password");

//     AuthenticationResponse response = authenticationService.register(request);

//     assertNotNull(response);
//     assertNotNull(response.getAccessToken());
//     assertEquals("Registration successful but email has to be verified", response.getMessage().trim());
// }


//     @Test
//     public void testAuthenticate() {
//         AuthenticationRequest request = new AuthenticationRequest();
//         request.setEmail("john.doe@example.com");
//         request.setPassword("password");

//         AuthenticationResponse response = authenticationService.authenticate(request);

//         assertNotNull(response);
//         assertNotNull(response.getAccessToken());
//         assertEquals("email has to be verfied", response.getMessage());
//     }

//     @Test
//     public void testVerifyEmailToken() {
//         String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXNoYWRiczNAZ21haWwuY29tIiwiaWF0IjoxNzEzMTgzMjQxLCJleHAiOjE3MTMxODY4NDF9.Yph4MDHRV9pHTm6I5RmtQCKfIE8kPzMLisT_aO_NsM0";

//         ResponseEntity<String> response = authenticationService.verifyEmailToken(token);

//         assertNotNull(response);
//         assertEquals("Email verified successfully", response.getBody());
//     }

//     @Test
//     public void testForgotPassword() {
//         EmailRequest request = new EmailRequest();
//         request.setEmail("john.doe@example.com");

//         AuthenticationResponse response = authenticationService.forgotPassword(request);

//         assertNotNull(response);
//         assertNotNull(response.getAccessToken());
//         assertEquals("link sent to your email for forgot password", response.getMessage());
//     }

//     @Test
//     public void testGeneratePassword() {
//         String token = "valid_token";

//         ResponseEntity<String> response = authenticationService.generatePassword(token);

//         assertNotNull(response);
//         assertEquals("RESET PASSWORD PAGE REDIRECTED successfully", response.getBody());
//     }

//     @Test
//     public void testResetPassword() {
//         String token = "valid_token";
//         String newPassword = "new_password";

//         ResponseEntity<String> response = authenticationService.resetPassword(token, newPassword);

//         assertNotNull(response);
//         assertEquals("RESET PASSWORD successfully", response.getBody());
//     }

//     @Test
//     public void testChangePassword() {
//         String email = "john.doe@example.com";
//         String oldPassword = "password";
//         String newPassword = "new_password";

//         ResponseEntity<String> response = authenticationService.changePassword(email, oldPassword, newPassword);

//         assertNotNull(response);
//         assertEquals("Password changed successfully for " + email, response.getBody());
//     }
// }

