package com.example.tongue.resources.testing;

import jdk.jfr.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class AppController {

    @GetMapping(value = "/app/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> makeLogin(HttpServletRequest request){
        log.info("App basic authentication");
        String base64token = request.getHeader("Authorization");
        String acceptValue = request.getHeader("Accept");
        String contentTypeVal = request.getHeader("Content-Type");
        log.info(String.valueOf("Accept Header: "+acceptValue==null));
        log.info(String.valueOf("Content-Type Header: "+contentTypeVal==null));
        User user = new User();
        user.username="alexander";
        user.photo="http://localhost:7070/imgs/12";
        if (base64token!=null)
            return new ResponseEntity<>(user,HttpStatus.OK);
        log.info("Authorization token not found");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}


class User{
    String username;
    String photo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
