package com.dantts.authenticationJWT.filter;

import com.dantts.authenticationJWT.entity.User;
import com.dantts.authenticationJWT.repository.UserRepository;
import com.dantts.authenticationJWT.service.JwtService;
import com.dantts.authenticationJWT.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@NoArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    @Autowired private JwtService jwtService;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        String headerAuthorization = request.getHeader("Authorization");

        if (headerAuthorization == null || !headerAuthorization.startsWith("Bearer")) {
            filter.doFilter(request, response);
            return;
        }

        String accessToken = headerAuthorization.replace("Bearer ", "");

        Long userFromToken = jwtService.getLoginFromToken(accessToken);



        Optional<User> userBanc = userRepository.findById(userFromToken);


        if (!userBanc.isPresent()){
            logger.error("Não foi possível encontrar o usuário");
            filter.doFilter(request, response);
        }

        response.setHeader("userLogin", userBanc.get().getLogin());
        response.setHeader("userId", String.valueOf(userBanc.get().getId()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userBanc.get().getLogin());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails,null,userDetails.getAuthorities()
        );

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filter.doFilter(request, response);
    }
}
