package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> securityConfigurerAdapter() {
        return new SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
            @Override
            public void configure(HttpSecurity http) {
                http.authenticationProvider(authenticationProvider());
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .authorizeRequests(authorizeRequests -> authorizeRequests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/api/url/**").authenticated()
                                .requestMatchers("/register").permitAll()
                                .requestMatchers("/perform_login").permitAll()
                                .requestMatchers("/submitContactForm").permitAll()
                                .requestMatchers("/forgot-password", "/reset-password").permitAll()
                                .requestMatchers("/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .formLogin(formLogin -> formLogin
                                .loginPage("/login").permitAll() // Specifies the custom login page URL
                                //.loginProcessingUrl("/perform_login") // Specifies the custom login processing URL
                                //.defaultSuccessUrl("/", true)
                                //.failureUrl("/login?error=true") // Redirect to login page with error parameter
                )
                .oauth2Login(oauth2Login -> oauth2Login
                            .loginPage("/login") // Customize login page if needed
                            .defaultSuccessUrl("/") // Customize default success URL after OAuth2 login
                            )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                       // .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                )
                .headers(headers->headers.frameOptions(frame->frame.disable()))      //enable iframe from anywhere
                .httpBasic(Customizer.withDefaults());
        //.authenticationProvider(authenticationProvider());

        return http.build();
    }

}