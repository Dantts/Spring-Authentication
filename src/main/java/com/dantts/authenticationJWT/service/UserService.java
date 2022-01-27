package com.dantts.authenticationJWT.service;

import com.dantts.authenticationJWT.entity.User;
import com.dantts.authenticationJWT.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public Optional<User> getUserByLogin(String login) {
       return userRepository.findByLogin(login);
    }
}
