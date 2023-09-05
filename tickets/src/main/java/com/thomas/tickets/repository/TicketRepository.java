package com.thomas.tickets.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomas.tickets.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}