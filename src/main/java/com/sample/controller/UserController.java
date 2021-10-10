package com.sample.controller;

import com.sample.security.annotation.UserRoles;
import com.sample.security.context.UserContext;
import com.sample.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserContext userContext;

    @GetMapping("/public-access")
    public ResponseEntity<User> publicAccess() {
        return new ResponseEntity<>(userContext.getUser(), HttpStatus.OK);
    }

    @UserRoles({"Minimal", "Admin"})
    @GetMapping("/minimal-access")
    public ResponseEntity<User> minimalAccess() {
        return new ResponseEntity<>(userContext.getUser(), HttpStatus.OK);
    }

    @UserRoles({"Admin"})
    @GetMapping("/only-admin")
    public ResponseEntity<User> onlyAdmin() {
        return new ResponseEntity<>(userContext.getUser(), HttpStatus.OK);
    }
}
