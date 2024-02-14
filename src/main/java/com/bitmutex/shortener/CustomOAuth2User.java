package com.bitmutex.shortener;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;

    public CustomOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("login");
    }

    // Additional methods to get custom user details

    public String getEmail() {
        return oauth2User.getAttribute("email");
    }

    public String getFirstName() {
        // Extract first name from GitHub attributes
        return oauth2User.getAttribute("name");
    }

    public String getLastName() {
        // Extract last name from GitHub attributes
        return "";
    }

    public String getUsername() {
        // Extract last name from GitHub attributes
        return oauth2User.getAttribute("login");
    }

    // Add more methods as needed to retrieve other user details

}
