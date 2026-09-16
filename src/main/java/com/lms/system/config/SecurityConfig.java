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

                // Authentication endpoints
                .requestMatchers("/api/auth/**")
                .permitAll()

                // Courses
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

                // Instructor/Admin enrollment management
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/enrollments/course/**"
                )
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/enrollments/course/**"
                )
                .hasAnyRole("INSTRUCTOR", "ADMIN")

                // Student/Admin enrollment endpoints
                .requestMatchers("/api/enrollments/**")
                .hasAnyRole("STUDENT", "ADMIN")

                // Dashboard
                .requestMatchers("/api/dashboard/**")
                .authenticated()

                // Everything else requires authentication
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

        // Frontend URLs allowed to communicate with the backend
        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173",
                "https://lms-frontend-srei.vercel.app"
            )
        );

        // Allowed HTTP methods
        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        // Allowed request headers
        configuration.setAllowedHeaders(
            List.of("*")
        );

        // Allow credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}