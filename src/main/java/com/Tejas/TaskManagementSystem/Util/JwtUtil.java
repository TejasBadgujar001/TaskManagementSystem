package com.Tejas.TaskManagementSystem.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//JWT has three main parts - Header, Signature, Payload
//Claims - information stored in Payload - {"sub":"tejas", "role":"MEMBER", "expiration":"1712345678"} Claims is basically like a map:
@Service
public class JwtUtil {

    @Value("${jwt.secret.key}") //Extract the Value of Secret Key from application.properties
    private String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public SecretKey getKey(){
        byte [] bytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor( bytes);
    }

    public String generateToken(String username){
        logger.info("Generating JWT token for user: {}", username);
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis()+1000*60*60);//1hr

        String token =Jwts.builder()
                .setClaims(Map.of())
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey())
                .compact();
        logger.info("JWT token generated successfully for user: {}", username);
        return token;
    }

    public Claims extractAllClaims(String token){
        logger.info("Extracting claims from JWT token");
        return Jwts.parser()    //Creates parser builder.
                .setSigningKey(getKey()) // Use this secret key to verify signature.
                .build()//Builds actual parser object.
                .parseSignedClaims(token)//Parses the JWT string and verify the signature, expiration, Structure
                .getPayload();// return Claims contains subject expiration issuedAt
    }

    //This is generic function here Function is Functional Interface have only one methods i.e apply()
    //this interface takes Claims as input and Return T as output that's generic
    public<T> T extractClaims(String token,Function<Claims,T>ClaimsResolver){
        Claims claims = extractAllClaims(token);
        return ClaimsResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        boolean expired = extractExpiration(token).before(new Date());
        if(expired){
            logger.warn("JWT token has expired");
        }
        return expired;
    }

    public boolean validateToken(String token, UserDetails userDetails){
        logger.info("Validating JWT token for user: {}", userDetails.getUsername());
        String username = extractUsername(token);
        boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        if(valid){
            logger.info("JWT token validated successfully for user: {}", username);
        }else {
            logger.warn("JWT token validation failed for user: {}", username);
        }
        return valid;
    }
}
