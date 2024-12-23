package ru.skillbox.mc_account.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.skillbox.mc_account.web.filter.AuthFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/account").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/account/total").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/account/me").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/v1/account").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/account").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/account/{id}").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/account/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/account/{id}").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
