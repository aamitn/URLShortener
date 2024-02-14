package com.bitmutex.shortener;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonLocked, boolean credentialsNonExpired, String email, String firstName, String lastName, Collection<? extends GrantedAuthority> authorities, Long userId ) {
        super(username, password, enabled, true, true, accountNonLocked, authorities);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
    }


    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public Long getUserId() {
        return userId;
    }
}
