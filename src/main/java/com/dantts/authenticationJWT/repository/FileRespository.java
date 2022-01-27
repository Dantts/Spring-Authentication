package com.dantts.authenticationJWT.repository;

import com.dantts.authenticationJWT.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRespository extends JpaRepository<File, Long> {
}
