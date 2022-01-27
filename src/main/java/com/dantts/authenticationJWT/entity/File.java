package com.dantts.authenticationJWT.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private String name;

  @Column
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String systemName;

  @Column private String linkDownload;

  @Column private Long size;

  @ManyToOne
  @JoinColumn(name = "userId")
  @JsonBackReference
  private User user;
}
