package com.nour.SpringBootMovieReservationSystem.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class SecurityConfiguration {

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select username,pw,active from customer where username=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username,role from roles where username=?");
        return jdbcUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(JdbcUserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsManager);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        httpSecurity.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET, "/customer/home").hasRole("CUSTOMER")
                .requestMatchers("/showCustomerLoginPage", "/showAdminLoginPage").permitAll()
                .anyRequest()
                .authenticated())
                .formLogin(form -> form
                        .loginPage("/showCustomerLoginPage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                        .permitAll())
                .exceptionHandling(configurer -> configurer.accessDeniedPage("/showAccessedDeniedPage"));

        return httpSecurity.build();

    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/movies/**", "/api/v1/showtimes/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservations/**").hasAnyRole("CUSTOMER", "EMPLOYEE")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public SecretKey jwtKey(@Value("${app.jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be atleast 32 bytes");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtKey) {
        return NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtkey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtkey));
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter granted = new JwtGrantedAuthoritiesConverter();
        granted.setAuthoritiesClaimName("roles");
        granted.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(granted);
        return converter;
    }
}
