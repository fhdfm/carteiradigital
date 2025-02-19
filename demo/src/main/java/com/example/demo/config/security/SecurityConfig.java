package com.example.demo.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
        AuthenticationProvider authenticationProvider) {
        
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                    req -> {
                        req.requestMatchers("/api/auth/**").permitAll();
                        req.anyRequest().authenticated();
                })
                .sessionManagement(
                    management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider);
        return http.build();
    }

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final AuthenticationProvider authenticationProvider;
//     private final JwtAuthFilter jwtAuthFilter;

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http.csrf(AbstractHttpConfigurer::disable)
//             .cors(AbstractHttpConfigurer::disable)
//             .authorizeHttpRequests(httpRequest -> {
//                 httpRequest.requestMatchers("/register", "/auth")
//                            .permitAll();
//                 httpRequest.requestMatchers(HttpMethod.POST)
//                            .hasAnyAuthority("ADMIN");
//                 httpRequest.anyRequest()
//                            .authenticated();
//             })
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//             .authenticationProvider(authenticationProvider);

//         return http.build();
//     }
// }

}
