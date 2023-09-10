package com.thomas.tickets.repository;


import com.thomas.tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TicketRepository extends CrudRepository<Ticket, Integer>, PagingAndSortingRepository<Ticket, Integer>, JpaSpecificationExecutor<Ticket> {

}