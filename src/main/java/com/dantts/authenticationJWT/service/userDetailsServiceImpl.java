package com.dantts.authenticationJWT.service;

import com.dantts.authenticationJWT.entity.User;
import com.dantts.authenticationJWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "customUserDetailsService")
public class userDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public userDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userExists = userRepository.findByLogin(username);

        if(!userExists.isPresent()) throw new UsernameNotFoundException("Usuario não encontrado");


        return new org.springframework.security.core.userdetails.User(
            userExists.get().getLogin(),
            userExists.get().getPassword(),
                AuthorityUtils.createAuthorityList(userExists.get().getRole())
        );


    }
}
