package com.dantts.authenticationJWT.configs;

import com.dantts.authenticationJWT.JwtAuthEntryPoint;
import com.dantts.authenticationJWT.bean.PasswordEncoder;
import com.dantts.authenticationJWT.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

  @Resource(name = "customUserDetailsService")
  private final UserDetailsService userDetailsService;

  private final JwtAuthEntryPoint unauthorizedHandler;

  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf().disable();
    httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

    httpSecurity.cors().configurationSource(corsConfigurationSource());

    httpSecurity
        .authorizeRequests()
        .antMatchers("/api/user/**")
        .permitAll()
        .antMatchers("/api/files/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/api/todo/**")
        .hasAnyAuthority("user", "admin")
        .antMatchers(HttpMethod.POST, "/api/todo/**")
        .hasAnyAuthority("admin")
        .antMatchers(HttpMethod.PUT, "/api/todo/**")
        .hasAnyAuthority("admin")
        .antMatchers(HttpMethod.DELETE, "/api/todo/**")
        .hasAnyAuthority("admin")
        .anyRequest()
        .authenticated();

    httpSecurity.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
    httpSecurity.addFilterAt(authFilterBean(), UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(encoder());
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(Collections.singletonList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
    configuration.setAllowedHeaders(
        Arrays.asList("Authentication", "Authorization", "Content-Type"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Bean
  public AuthFilter authFilterBean() {
    return new AuthFilter();
  }

  @Bean
  public PasswordEncoder encoder() {
    return new PasswordEncoder();
  }
}
