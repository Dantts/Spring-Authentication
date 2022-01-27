package com.dantts.authenticationJWT.controller;

import com.dantts.authenticationJWT.entity.User;
import com.dantts.authenticationJWT.repository.UserRepository;
import com.dantts.authenticationJWT.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

  private UserRepository userRepository;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private JwtService jwtService;

  @GetMapping("/{id}")
  public User getUser(@PathVariable Long id) {
    return userRepository.findById(id).get();
  }

  @PostMapping("/sign-in")
  public ResponseEntity<HashMap<String, Object>> signin(@RequestBody User user) {
    Optional<User> userBanc = userRepository.findByLogin(user.getLogin());
    Boolean matches =
        bCryptPasswordEncoder.matches(user.getPassword(), userBanc.get().getPassword());
    if (!matches) {
      return ResponseEntity.status(404).build();
    }

    String token = jwtService.generateToken(userBanc.get());

    HashMap<String, Object> response = new HashMap<>();

    if (token == null && !token.startsWith("Bearer")) {
      return ResponseEntity.status(500).build();
    }

    response.put("data", userBanc);
    response.put("token", token);

    return ResponseEntity.status(200).body(response);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<HashMap<String, Object>> signup(@RequestBody User user) {
    HashMap<String, Object> userResponse = new HashMap<>();
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    User userBanc = userRepository.save(user);

    String token = jwtService.generateToken(userBanc);

    userResponse.put("user", userBanc);
    userResponse.put("token", token);

    return ResponseEntity.status(HttpStatus.OK).body(userResponse);
  }
}
