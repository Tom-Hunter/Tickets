package com.thomas.tickets.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tiko")
public class UserController {


    /**
     * Endpoint for fetching all user
     */
    @GetMapping("/user")
    public String getAllUser() {
        return "This is a user";
    }

}