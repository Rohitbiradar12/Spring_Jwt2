package com.thbs.applicationgateway.util;

import java.security.Key;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// import com.thbs.security.user.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
@Component
public class JwtUtil {


    public static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";


    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }



    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}


// @Component
// public class JwtUtil {

//     public static final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    

//     // Method to extract username from JWT token
//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     // Method to extract specific claim from JWT token
//     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimsResolver.apply(claims);
//     }

//     // Method to generate JWT token
//     // public String generateToken(UserDetails userDetails) {
//     //     return generateToken(new HashMap<>(), userDetails);
//     // }

 



//     // Method to build JWT token
    

// // Method to check if JWT token is expired
// public boolean isTokenExpired(String token) {
//     Date expirationDate = extractExpiration(token);
//     return expirationDate != null && expirationDate.before(new Date());
// }

// // Method to validate JWT token
// // public boolean isTokenValid(String token, UserDetails userDetails) {
// //     final String username = extractUsername(token);
// //     return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
// // }



//     // Method to extract token expiration date from JWT token
//     private Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }

//     // Method to extract all claims from JWT token
//     private Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(secretKey)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }
// }