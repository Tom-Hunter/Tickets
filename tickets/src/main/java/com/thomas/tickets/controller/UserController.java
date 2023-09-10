package com.thomas.tickets.controller;

import com.thomas.tickets.model.User;
import com.thomas.tickets.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@RestController
@RequestMapping("/tiko")
public class UserController {


    @Autowired
    private UserService userService;


    /**
     * Endpoint for fetching all user
     */
    @GetMapping("/user")
    public ResponseEntity<Stream<User>> getAllUser() {
        List<User> user = userService.getAllUser();
        if (user.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(user.stream());
        }
    }

    /**
     * Endpoint for fetching a user with an id
     * @param id
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUser(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Endpoint for adding a user
     * @param user
     * @ResponseBody
     */
    @PostMapping("/user")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try{
            User createdUser = userService.addUser(user);
            log.info("User with user id: " + user.getId() + " Created!");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.info("Internal server error while creating user with id: " + user.getId());
            log.info("Server error: " + e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint for updating a user using an id
     * @param user
     * @param id
     *
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Integer id) {
        if(true == userService.checkIfUserExists(id)){
            User updatedUser = userService.updateUser(id, user);
            log.info("User with user id: " + id.toString() + " updated!");
            return ResponseEntity.ok().body(updatedUser);
        } else {
            log.info("User with user id: " + id.toString() + " not found!");
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Endpoint for deleting a user using an id
     * @param id
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        if(true == userService.checkIfUserExists(id)){
            userService.deleteUser(id);
            log.info("'User with user Id: " +id.toString() + " deleted succesfully'");
            return ResponseEntity.ok().body("'User with user Id: " +id.toString() + " deleted succesfully'");
        } else {
            log.info("User with user id: " + id.toString() + " not found!");
            return ResponseEntity.notFound().build();
        }

    }
}