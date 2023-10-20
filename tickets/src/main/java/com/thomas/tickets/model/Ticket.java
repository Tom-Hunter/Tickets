package com.thomas.tickets.model;

import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.thomas.tickets.enums.PriorityLevel;
import com.thomas.tickets.enums.TicketCategory;
import com.thomas.tickets.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    private String title;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;

    private int owneruserId;

    private int assigneduserId;

    private String body;

    @Enumerated(EnumType.ORDINAL)
    private TicketCategory ticketCategory;

    @Enumerated(EnumType.ORDINAL)
    private TicketStatus ticketStatus;

}