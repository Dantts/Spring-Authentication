package com.dantts.authenticationJWT.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "login")
  private String login;

  @Column(name = "password")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(name = "role")
  private String role;

  @OneToMany(mappedBy = "user")
  @JsonManagedReference
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private List<File> files;
}
