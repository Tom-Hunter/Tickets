package com.thomas.tickets.controller;

import com.thomas.tickets.DTO.TicketWithComments;
import com.thomas.tickets.model.Ticket;
import com.thomas.tickets.model.User;
import com.thomas.tickets.repository.UserRepository;
import com.thomas.tickets.services.SendMailService;
import com.thomas.tickets.services.TicketService;
import com.thomas.tickets.util.PagingHeaders;
import com.thomas.tickets.util.PagingResponse;
import jakarta.transaction.Transactional;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/tiko")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/")
    public String homePage(){
        return "Welcome to Tiko Tickets.";
    }

    /**
     * Endpoint for fetching all Tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<Stream<Ticket>> getAllTicket() {
        List<Ticket> ticket = ticketService.getTickets();
        if (ticket.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(ticket.stream());
        }
    }

    /**
     * Endpoint for fetching all Tickets, sorting and filtering Tickets.
     */

    @Transactional
    @GetMapping(value = "/getTickets", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Ticket>> get(
            @And({
                    @Spec(path = "title", params = "title", spec = Equal.class),
                    @Spec(path = "owneruserId", params = "owneruserId", spec = Equal.class),
                    @Spec(path = "ticketStatus", params = "ticketStatus", spec = In.class),
                    @Spec(path = "ticketCategory", params = "ticketCategory", spec = Equal.class),
                    @Spec(path = "createDate", params = "createDate", spec = Equal.class),
                    @Spec(path = "timeTaken", params = {"createdDate", "modifiedDate"}, spec = Between.class)
            }) Specification<Ticket> spec,
            Sort sort,
            @RequestHeader HttpHeaders headers) {

        final PagingResponse response = ticketService.get(spec, headers, sort);
        return new ResponseEntity<>(response.getElements(), returnHttpHeaders(response), HttpStatus.OK);
    }

    public HttpHeaders returnHttpHeaders(PagingResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PagingHeaders.COUNT.getName(), String.valueOf(response.getCount()));
        headers.set(PagingHeaders.PAGE_SIZE.getName(), String.valueOf(response.getPageSize()));
        headers.set(PagingHeaders.PAGE_OFFSET.getName(), String.valueOf(response.getPageOffset()));
        headers.set(PagingHeaders.PAGE_NUMBER.getName(), String.valueOf(response.getPageNumber()));
        headers.set(PagingHeaders.PAGE_TOTAL.getName(), String.valueOf(response.getPageTotal()));
        return headers;
    }


    /**
     * Endpoint for fetching a ticket with an id
     * @param id
     */
    @GetMapping("/getTicketWithId/{id}")
    public ResponseEntity<TicketWithComments> getTicketById(@PathVariable Integer id) {
        TicketWithComments ticket = ticketService.getTicket(id);
        // if (1=1) {
        return ResponseEntity.ok(ticket);
        // } else {
        //     return ResponseEntity.notFound().build();
        // }
    }


    /**
     * Endpoint for adding a ticket
     * @param ticket
     * @ResponseBody
     */
    @PostMapping("/raiseticket/{id}")
    public ResponseEntity<Ticket> raiseTicket(@RequestBody Ticket ticket, @PathVariable Integer id) {
        try{
            Ticket createdTicket = ticketService.raiseTicket(ticket, id);

            Optional<User> userRaisingTicket = userRepository.findById(id);

            log.info("Ticket with ticket id: " + ticket.getId() + " Created!");

            //Send an email
            sendMailService.sendSimpleMessage(
                    //Email Recipient
                    userRaisingTicket.get().getEmail().toString(),

                    //Email Subject
                    "Ticket " + ticket.getId() + " Raised",

                    //Email body
                    "This is a confirmation mail that your ticket has been raised with the ticket id:" + ticket.getId() + "\n\n Regards: \n Tiko Ticketing System"

            );

            log.info("Mail to raise ticket with ticket id: " + ticket.getId() + " sent succesfully!");

            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (Exception e) {
            log.info("Internal server error while creating ticket with id: " + ticket.getId());
            log.info("Server error: " + e.toString());
            return ResponseEntity.internalServerError().body(ticket);
        }
    }

    /**
     * Endpoint for testing email.
     */
    @PostMapping("/testEmail")
    public void testEmail() {

        sendMailService.sendSimpleMessage("test@gmail.com", "Test email", "This is a test email");

    }

    /**
     * Endpoint for updating a ticket using an id
     * @param ticket
     * @param id
     *
     */
    @PutMapping("/ticket/{id}")
    public ResponseEntity<Ticket> updateTicket(@RequestBody Ticket ticket, @PathVariable Integer id) {

        if(true == ticketService.checkIfTicketExists(id)){
            Ticket updatedticket = ticketService.updateTicket(id, ticket);
            log.info("Ticket with ticket id: " + id.toString() + " updated!");
            return ResponseEntity.ok().body(updatedticket);

        } else {
            log.info("Ticket with ticket id: " + id.toString() + " not found!");
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Endpoint for acknowledging a ticket using an id
     * @param ticketId
     * @param assignedToId
     *
     */
    @PutMapping("/acknowledgeTicket/{ticketId}/{assignedToId}")
    public ResponseEntity<Ticket> acknowledgeTicket(@PathVariable Integer ticketId, @PathVariable Integer assignedToId) {

        if(true == ticketService.checkIfTicketExists(ticketId)){
            Ticket updatedTicket = ticketService.acknowledgeTicket(ticketId, assignedToId);

            Optional<User> userRaisingTicket = userRepository.findById(updatedTicket.getOwneruserId());

            Optional<User> assignedTo = userRepository.findById(assignedToId);

            //Send an email
            sendMailService.sendSimpleMessage(
                    //Email Recipient
                    userRaisingTicket.get().getEmail().toString(),

                    //Email Subject
                    "Ticket " + updatedTicket.getId() + " Assigned",

                    //Email body
                    "This is a confirmation mail that your ticket with the ticket id:" + updatedTicket.getId() + " has been assigned to: \n\n" +assignedTo.get().getName().toString()+ "\n" +assignedTo.get().getEmail().toString()+ "\n\n Regards: \n Tiko Ticketing System"

            );

            log.info("Mail to acknowledge ticket with ticket id: " + ticketId.toString() + " sent succesfully!");

            log.info("Ticket with ticket id: " + ticketId.toString() + " acknowledged!");
            return ResponseEntity.ok().body(updatedTicket);

        } else {
            log.info("Ticket with ticket id: " + ticketId.toString() + " not found!");
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Endpoint for closing a ticket using an id
     * @param id
     *
     */
    @PutMapping("/closeTicket/{id}")
    public ResponseEntity<Ticket> closeTicket(@PathVariable Integer id) {

        if(true == ticketService.checkIfTicketExists(id)){
            Ticket updatedTicket = ticketService.closeTicket(id);
            log.info("Ticket with ticket id: " + id.toString() + " Closed!");

            Optional<User> userRaisingTicket = userRepository.findById(updatedTicket.getOwneruserId());

            //Send an email
            sendMailService.sendSimpleMessage(
                    //Email Recipient
                    userRaisingTicket.get().getEmail().toString(),

                    //Email Subject
                    "Ticket " + updatedTicket.getId() + " Closed",

                    //Email body
                    "This is a confirmation mail that your ticket with the ticket id:" + updatedTicket.getId() + " has been closed. \n\n Regards: \n Tiko Ticketing System"

            );

            log.info("Mail to close the ticket with ticket id: " + updatedTicket.getId() + " sent succesfully!");

            return ResponseEntity.ok().body(updatedTicket);

        } else {
            log.info("Ticket with ticket id: " + id.toString() + " not found!");
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Endpoint for deleting a ticket using an id
     * @param id
     */
    @DeleteMapping("/ticket/{id}")
    public ResponseEntity<String> deleteticket(@PathVariable Integer id) {
        if(true == ticketService.checkIfTicketExists(id)){
            //Delete ticket if it exists
            ticketService.deleteTicket(id);
            log.info("Ticket with ticket Id: " + id.toString() + " deleted succesfully'");
            return ResponseEntity.ok().body("Ticket with ticket Id: " + id.toString() + " deleted succesfully'");

        } else {
            //Return a not found response if user does not exist
            log.info("Ticket with ticket id: " + id.toString() + " not found!");
            return ResponseEntity.notFound().build();
        }

    }














}