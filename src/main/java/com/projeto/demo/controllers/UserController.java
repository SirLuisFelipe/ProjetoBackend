package com.projeto.demo.controllers;

import com.projeto.demo.dto.UserRegisterDto;
import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody UserRegisterDto userDto) {
        try {
            return ResponseEntity.ok(userService.update(userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> listUsers() {
        try {
            return ResponseEntity.ok(userService.listUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam("name") String name) {
        try {
            // if (!isAdmin()) {
            //     throw new UnauthorizedActionException();
            //}

            return ResponseEntity.ok(userService.searchUsersByName(name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        try {
            if (!isAdmin()) {
                throw new UnauthorizedActionException();
            }

            userService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
