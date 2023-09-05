package com.thomas.tickets.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tiko")
public class TicketController {


    /**
     * Endpoint for fetching all user
     */
    @GetMapping("/ticket")
    public String getAllTickets() {
        return "This is a ticket";
    }

}