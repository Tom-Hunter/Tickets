package com.thomas.tickets.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.thomas.tickets.DTO.TicketWithComments;
import com.thomas.tickets.enums.TicketStatus;
import com.thomas.tickets.model.Ticket;
import com.thomas.tickets.repository.TicketRepository;
import com.thomas.tickets.util.PagingHeaders;
import com.thomas.tickets.util.PagingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket raiseTicket(Ticket ticket, Integer id){

        //Set the time of creating the ticket to be the current time.
        ticket.setCreatedDate(LocalDateTime.now());
        ticket.setModifiedDate(LocalDateTime.now());

        // ticket.setComments(null);

        ticket.setOwneruserId(id);

        return ticketRepository.save(ticket);
    }

    public ArrayList<Ticket> getTickets(){
        ArrayList<Ticket> tickets = new ArrayList<> ();
        ticketRepository.findAll()
                .forEach(tickets::add);
        return tickets;
    }

    public TicketWithComments getTicket(Integer id) {

        /// Fetch the Ticket by Id
        Ticket ticketBeforeUpdate = ticketRepository.findById(id).orElseThrow();

        TicketWithComments ticketWithComments = new TicketWithComments();

        ticketWithComments.setId(ticketBeforeUpdate.getId());
        ticketWithComments.setCreatedDate(ticketBeforeUpdate.getCreatedDate());
        ticketWithComments.setModifiedDate(ticketBeforeUpdate.getModifiedDate());
        ticketWithComments.setTitle(ticketBeforeUpdate.getTitle());
        ticketWithComments.setBody(ticketBeforeUpdate.getBody());
        ticketWithComments.setOwneruserId(ticketBeforeUpdate.getOwneruserId());
        ticketWithComments.setAssigneduserId(ticketBeforeUpdate.getAssigneduserId());
        ticketWithComments.setTicketCategory(ticketBeforeUpdate.getTicketCategory());
        ticketWithComments.setTicketStatus(ticketBeforeUpdate.getTicketStatus());

        // ArrayList<Comment> listOfComments = new ArrayList<Comment>();

        // listOfComments.add(ticketBeforeUpdate.getComments());

        // ticketWithComments.setListOfComments(listOfComments);

        return ticketWithComments;
    }

    /* get element using Criteria.
     *
     * @param spec    *
     * @param headers pagination data
     * @param sort    sort criteria
     * @return retrieve elements with pagination
     */
    public PagingResponse get(Specification<Ticket> spec, HttpHeaders headers, Sort sort) {
        if (isRequestPaged(headers)) {
            return get(spec, buildPageRequest(headers, sort));
        } else {
            final List<Ticket> entities = get(spec, sort);
            return new PagingResponse((long) entities.size(), 0L, 0L, 0L, 0L, entities);
        }
    }

    private boolean isRequestPaged(HttpHeaders headers) {
        return headers.containsKey(PagingHeaders.PAGE_NUMBER.getName()) && headers.containsKey(PagingHeaders.PAGE_SIZE.getName());
    }

    private Pageable buildPageRequest(HttpHeaders headers, Sort sort) {
        int page = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_NUMBER.getName())).get(0));
        int size = Integer.parseInt(Objects.requireNonNull(headers.get(PagingHeaders.PAGE_SIZE.getName())).get(0));
        return PageRequest.of(page, size, sort);
    }

    /**
     * get elements using Criteria.
     *
     * @param spec     *
     * @param pageable pagination data
     * @return retrieve elements with pagination
     */
    public PagingResponse get(Specification<Ticket> spec, Pageable pageable) {
        Page<Ticket> page = ticketRepository.findAll(spec, pageable);
        List<Ticket> content = page.getContent();
        return new PagingResponse(page.getTotalElements(), (long) page.getNumber(), (long) page.getNumberOfElements(), pageable.getOffset(), (long) page.getTotalPages(), content);
    }

    /**
     * get elements using Criteria.
     *
     * @param spec *
     * @return elements
     */
    public List<Ticket> get(Specification<Ticket> spec, Sort sort) {
        return ticketRepository.findAll(spec, sort);
    }


    public Ticket updateTicket(Integer id, Ticket ticket) {
        /// Fetch the Ticket by Id
        Ticket ticketBeforeUpdate = ticketRepository.findById(id).orElseThrow();

        /// Update the Ticket using Getters and Setters
        ticketBeforeUpdate.setTitle(ticket.getTitle());
        ticketBeforeUpdate.setBody(ticket.getBody());
        ticketBeforeUpdate.setOwneruserId(ticket.getOwneruserId());
        ticketBeforeUpdate.setTicketCategory(ticket.getTicketCategory());
        ticketBeforeUpdate.setTicketStatus(ticket.getTicketStatus());

        //Update the modified date with the current date
        ticket.setModifiedDate(LocalDateTime.now());

        /// Update the Ticket
        Ticket updatedTicket = ticketRepository.save(ticketBeforeUpdate);

        /// Return the updated Ticket as a response
        return updatedTicket;
    }


    public Ticket acknowledgeTicket(Integer ticketId, Integer assignedToId) {

        /// Fetch the Ticket by Id
        Ticket ticketBeforeAcknowledge = ticketRepository.findById(ticketId).orElseThrow();

        //Update the modified date with the current date
        ticketBeforeAcknowledge.setModifiedDate(LocalDateTime.now());

        //Assign ticket
        ticketBeforeAcknowledge.setAssigneduserId(assignedToId);

        //Update the ticket status with a "pending" enum
        ticketBeforeAcknowledge.setTicketStatus(TicketStatus.PENDING);

        /// Acknowledge the Ticket
        Ticket acknowledgedTicket = ticketRepository.save(ticketBeforeAcknowledge);

        /// Return the acknowledged Ticket as a response
        return acknowledgedTicket;
    }

    public Ticket closeTicket(Integer id) {

        /// Fetch the Ticket by Id
        Ticket ticketBeforeclosing = ticketRepository.findById(id).orElseThrow();

        //Update the modified date with the current date
        ticketBeforeclosing.setModifiedDate(LocalDateTime.now());

        //Update the ticket status with a "pending" enum
        ticketBeforeclosing.setTicketStatus(TicketStatus.CLOSED);

        /// Close the Ticket
        Ticket closedTicket = ticketRepository.save(ticketBeforeclosing);

        /// Return the closed Ticket as a response
        return closedTicket;
    }

    public void deleteTicket(Integer id) {
        if(ticketRepository.existsById(id)){
            ticketRepository.deleteById(id);
        } else {
            log.info("Ticket does not exist.");
        }
    }

    public Boolean checkIfTicketExists(Integer id) {
        if(ticketRepository.existsById(id)){
            return true;
        } else {
            return false;
        }
    }

}