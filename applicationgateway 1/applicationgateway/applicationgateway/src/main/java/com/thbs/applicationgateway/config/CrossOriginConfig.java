// package com.thbs.applicationgateway.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// // import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
// @Configuration
// public class CrossOriginConfig implements WebMvcConfigurer{
//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**") // Allow CORS for all endpoints
//                 .allowedOrigins("*") // Allow requests from any origin
//                 .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed HTTP methods
//                 .allowedHeaders("*")
//                 .exposedHeaders("Authorization")
//                 .maxAge(3600)
//                 .allowCredentials(false); // Allowed headers
//     }
   
// }