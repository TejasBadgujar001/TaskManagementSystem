package com.Tejas.TaskManagementSystem.Service;

import com.Tejas.TaskManagementSystem.Entity.UserEntity;
import com.Tejas.TaskManagementSystem.Exception.ResourceNotFoundException;
import com.Tejas.TaskManagementSystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
     private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity= userRepository.findByEmail(username)
                .orElseThrow(()->new ResourceNotFoundException("No User Found with username: "+username ));
        return User.builder()
                .username(entity.getEmail())
                .password(entity.getPassword())
                .authorities(getAuthorities(entity.getRole().name()))
                .build();
    }
    private Collection<SimpleGrantedAuthority> getAuthorities(String role){  //SimpleGrantedAuthority: Spring Security Class-> It represents ONE authority.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
