package com.bitmutex.shortener;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService  {
    final
    UserRepository userRepository;

    final
    UserService userService;

    public CustomOAuth2UserService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);


        Map<String, Object> attributes = user.getAttributes();

        System.out.println(attributes.toString());

        String username = (String) attributes.get("login");
        System.out.println("Username:" + username);
        loadUserByOAuth2UserRequest(userRequest);

        return new CustomOAuth2User(user);
    }
    public CustomUserDetails loadUserByOAuth2UserRequest(OAuth2UserRequest oAuth2UserRequest) {
        OAuth2User oAuth2User =  super.loadUser(oAuth2UserRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("login"); // Adjust based on the provider
        System.out.println(username);

        // Extract other attributes as needed

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setUsername(username);
            registrationRequest.setEmail(username+"@youremail.tld");
            registrationRequest.setFirstName(username);
            registrationRequest.setLastName(username);
            registrationRequest.setPassword(username+ randAlphaString() );
            //todo
            user = registerNewOAuth2User(registrationRequest);
        }



        return new CustomUserDetails(
                user.getUsername(),
                "", // You may need to adjust this if your OAuth2 login doesn't have a password
                true, // Assuming OAuth2 login is always enabled
                true, // Assuming OAuth2 login is never locked
                true, // Assuming OAuth2 login credentials never expire
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                user.getId()
        );
    }

    private UserEntity registerNewOAuth2User(RegistrationRequest request) {
        // Implement logic to register a new user based on OAuth2 attributes
        return userService.registerNewUser(request);
    }

    public String randAlphaString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}