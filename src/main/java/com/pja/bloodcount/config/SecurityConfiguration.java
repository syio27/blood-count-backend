package com.pja.bloodcount.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("localdb")
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //whitelist of endpoints
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                //Session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //which authentication provider will be used
                .authenticationProvider(authenticationProvider)
                .csrf().disable()
                .httpBasic().disable()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().sameOrigin();

        return http.build();
    }
}