package com.dantts.authenticationJWT.controller;

import com.dantts.authenticationJWT.entity.File;
import com.dantts.authenticationJWT.entity.User;
import com.dantts.authenticationJWT.repository.FileRespository;
import com.dantts.authenticationJWT.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController {

  private UserRepository userRepository;
  private FileRespository fileRespository;

  @GetMapping
  public ResponseEntity<List<File>> getAllFiles(HttpServletRequest req, HttpServletResponse res) {
    Long id = Long.valueOf(res.getHeader("userId"));

    Optional<User> user = userRepository.findById(id);

    if (!user.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(user.get().getFiles());
  }

  @GetMapping("/download/{id}/{userLogin}/{fileName:.*}")
  public ResponseEntity<?> downloads(
      HttpServletRequest req,
      HttpServletResponse res,
      @PathVariable Long id,
      @PathVariable String userLogin,
      @PathVariable String fileName) {
    Path directory =
        Paths.get(System.getProperty("user.dir") + "/temp/uploads/" + userLogin + "/" + fileName);
    Resource resource = null;

    try {
      resource = new UrlResource(directory.toUri());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    Optional<File> file = fileRespository.findById(id);

    return ResponseEntity.status(HttpStatus.OK)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.get().getName() + "\"")
        .body(resource);
  }

  @PostMapping("/uploads")
  public ResponseEntity<List<File>> multiUploads(
      HttpServletRequest req,
      HttpServletResponse res,
      @RequestParam("files") MultipartFile[] files) {
    Long id = Long.valueOf(res.getHeader("userId"));
    Optional<User> user = userRepository.findById(id);

    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<File> filesList = new ArrayList<>();

    Arrays.asList(files)
        .forEach(
            file -> {
              filesList.add(upload(user.get(), file));
            });

    return ResponseEntity.status(HttpStatus.OK).body(filesList);
  }

  public File upload(User user, MultipartFile file) {

    File fileObject = new File();

    UUID randomUUID = UUID.randomUUID();
    String formatUUID = randomUUID.toString().replace("-", "");

    String fileName =
        formatUUID
            + "-"
            + file.getOriginalFilename().replace(" ", "").replace("(", "").replace(")", "");

    Path directory = Paths.get(System.getProperty("user.dir"), "/temp/uploads/", user.getLogin());
    Path path = directory.resolve(fileName);

    try {
      Files.createDirectories(directory);
      file.transferTo(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    fileObject.setName(file.getOriginalFilename());
    fileObject.setSystemName(fileName);
    fileObject.setSize(file.getSize());

    File newFile = fileRespository.save(fileObject);

    String linkDownload =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/files/download/")
            .path(newFile.getId().toString())
            .path("/")
            .path(user.getLogin())
            .path("/")
            .path(fileName)
            .toUriString();

    newFile.setLinkDownload(linkDownload);
    newFile.setUser(user);

    return fileRespository.save(newFile);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteFile(@PathVariable Long id) {
    Optional<File> file = fileRespository.findById(id);

    if (!file.isPresent()) {
      ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    Path directory =
        Paths.get(
            System.getProperty("user.dir"),
            "/temp/uploads/",
            file.get().getUser().getLogin(),
            "/",
            file.get().getSystemName());

    Boolean deletedFile = false;
    try {
      deletedFile = Files.deleteIfExists(directory);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (!deletedFile) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    fileRespository.deleteById(id);
    return ResponseEntity.status(HttpStatus.OK).body(deletedFile);
  }
}
