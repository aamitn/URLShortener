package com.bitmutex.shortener;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));



        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                true, // Assuming credentials never expire; you can change this based on your requirements
                user.getEmail(),
                user.getFirstName(), // Include first name
                user.getLastName(), // Include last name
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                user.getId()
        );
    }

}
