package com.thomas.tickets.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import com.thomas.tickets.model.User;
import com.thomas.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * A method to fetch all UserS
     * @return ArrayList of UserS
     */
    public ArrayList<User> getAllUser() {
        ArrayList<User> user = new ArrayList<> ();
        userRepository.findAll()
                .forEach(user::add);
        return user;
    }

    /**
     * A method to fetch a specific user
     * @param id
     * @return
     */
    public Optional<User> getUser(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * A method to create a user
     *
     * @param user
     * @return
     */
    public User addUser(User user) {

        //Set the time of creating the ticket to be the current time.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user.setModifiedDate(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * A method to update a user
     * @param id
     * @param user
     *
     * @return Updated User
     */
    public User updateUser(Integer id, User user) {
        /// Fetch the user by Id
        User userBeforeUpdate = userRepository.findById(id).orElseThrow();

        /// Update the user using Getters and Setters
        userBeforeUpdate.setEmail(user.getEmail());
        userBeforeUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        userBeforeUpdate.setUserRole(user.getUserRole());
        userBeforeUpdate.setName(user.getName());

        //Update the modified date with the current date
        userBeforeUpdate.setModifiedDate(LocalDateTime.now());

        /// Update the user
        User updatedUser = userRepository.save(userBeforeUpdate);

        /// Return the updated User as a response
        return updatedUser;
    }

    /**
     * A method to delete a user
     * @param id
     */
    public void deleteUser(Integer id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        } else {
            log.info("User does not exist.");
        }
    }

    public Boolean checkIfUserExists(Integer id) {
        if(userRepository.existsById(id)){
            return true;
        } else {
            return false;
        }
    }
}