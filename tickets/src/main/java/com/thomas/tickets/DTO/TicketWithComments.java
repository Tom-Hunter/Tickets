package com.thomas.tickets.DTO;

import java.time.LocalDateTime;
import java.util.List;


import com.thomas.tickets.enums.TicketCategory;
import com.thomas.tickets.enums.TicketStatus;
import com.thomas.tickets.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketWithComments {

    private int id;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String title;

    private int owneruserId;

    private int assigneduserId;

    private String body;

    private TicketCategory ticketCategory;

    private TicketStatus ticketStatus;

    private List<Comment> listOfComments;
}

