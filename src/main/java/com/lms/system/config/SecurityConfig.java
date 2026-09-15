package com.lms.system.config;

import com.lms.system.security.JwtAuthFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/api/auth/**")
                .permitAll()

                .requestMatchers(HttpMethod.GET, "/api/courses/**")
                .authenticated()

                .requestMatchers(HttpMethod.POST, "/api/courses/**")
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/courses/**")
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/courses/**")
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                // Admin-only endpoints
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/enrollments/course/**")
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/enrollments/course/**")
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                .requestMatchers("/api/enrollments/**")
                .hasAnyRole("STUDENT", "ADMIN")

                .requestMatchers("/api/dashboard/**")
                .authenticated()

                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
            List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}