package com.Tejas.TaskManagementSystem.Security;

import com.Tejas.TaskManagementSystem.Service.CustomUserDetailsService;
import com.Tejas.TaskManagementSystem.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if(path.equals("/user/signup") || path.equals("/user/login")){
            logger.info("Public endpoint accessed: {}", path);
            filterChain.doFilter(request,response);
            return;
        }

        logger.info("Processing JWT authentication for request: {}", path);

        String token = null;
        String email = null;
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            email = jwtUtil.extractUsername(token);
            logger.info("JWT token extracted successfully for user: {}", email);
        }else{
            logger.warn("Authorization header missing or invalid");
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class).loadUserByUsername(email);
            if(jwtUtil.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User authenticated successfully with email: {}", email);
            }else{
                logger.warn("JWT token validation failed for user: {}", email);
            }
        }
        filterChain.doFilter(request,response);
    }
}
