package ru.otus.hw.security.configurations;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfiguration {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        // Enable free access to H2 console
                        authorize -> authorize
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .headers(headers -> headers
                        .frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return httpSecurity.build();
    }

    // Argon2 Hash Generator & Verifier - https://argon2.online/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
