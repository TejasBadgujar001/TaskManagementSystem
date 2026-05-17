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
//JWT has three main parts - Header, Signature, Payload
//Claims - information stored in Payload - {"sub":"tejas", "role":"MEMBER", "expiration":"1712345678"} Claims is basically like a map:
@Service
public class JwtUtil {

    @Value("${jwt.secret.key}") //Extract the Value of Secret Key from application.properties
    private String secretKey;

    public SecretKey getKey(){
        byte [] bytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor( bytes);
    }

    public String generateToken(String username){
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis()+1000*60*60);//1hr
        return Jwts.builder()
                .setClaims(Map.of())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey())
                .compact();
    }

    public Claims extractAllClaims(String token){
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
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
