package com.dantts.authenticationJWT.bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordEncoder extends BCryptPasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder(10);

        final String password = rawPassword.toString();

        return bcryptEncoder.encode(password);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder(10);

        return bcryptEncoder.matches(rawPassword, encodedPassword);
    }

}
