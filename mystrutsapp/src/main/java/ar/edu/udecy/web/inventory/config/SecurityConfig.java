package ar.edu.udecy.web.inventory.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login").permitAll() // Permitir login sin autenticación
                        .requestMatchers("/", "/**.html", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/users").hasRole("ADMIN") // Solo ROLE_ADMIN puede acceder a /users
                        .anyRequest().authenticated()) // Requerir autenticación para otros endpoints
                .headers(AbstractHttpConfigurer::disable) // Necesario para H2 Console
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}