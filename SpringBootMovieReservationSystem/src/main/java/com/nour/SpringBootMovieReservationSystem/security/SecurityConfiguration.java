package com.nour.SpringBootMovieReservationSystem.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
}
