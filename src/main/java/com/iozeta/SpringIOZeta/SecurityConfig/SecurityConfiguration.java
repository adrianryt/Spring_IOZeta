package com.iozeta.SpringIOZeta.SecurityConfig;

import com.iozeta.SpringIOZeta.SecurityConfig.filter.CustomAuthenticationFilter;
import com.iozeta.SpringIOZeta.SecurityConfig.filter.CustomAuthorizationFilter;
import com.iozeta.SpringIOZeta.SecurityConfig.filter.SimpleCORSFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

   private final UserDetailsService userDetailsService;
   private final BCryptPasswordEncoder bCryptPasswordEncoder;
   private final SimpleCORSFilter simpleCORSFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        narazie mamy tutaj takie demu uprawnień, dokładnie określimy to po ustaleniu szczegółów
//        jak dajemy permit all nie potrzeba logowania do dostępu tam
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS,"/**").permitAll();
        http.authorizeRequests().antMatchers("/api/login/**","/api/token/refresh/**","/api/lecturer/save", "/student/add-to-session").permitAll();
        http.authorizeRequests().antMatchers("/api/lecturers", "/task/add",  "/subjects/**").hasAnyAuthority("LECTURER");
        http.authorizeRequests().antMatchers("/sessions/**").permitAll();
        http.authorizeRequests().antMatchers("/git/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(simpleCORSFilter, CustomAuthorizationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
