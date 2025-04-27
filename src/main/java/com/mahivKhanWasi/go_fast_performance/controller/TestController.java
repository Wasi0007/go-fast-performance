package com.mahivKhanWasi.go_fast_performance.controller;


import com.mahivKhanWasi.go_fast_performance.repository.Ticket1Repository;
import com.mahivKhanWasi.go_fast_performance.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {

    private final TicketRepository ticketRepository;
    private final Ticket1Repository ticket1Repository;

    @GetMapping("/ticket")
    public ResponseEntity<?> getTicket(){
        return new ResponseEntity<>(ticketRepository.findAll().size(), HttpStatus.OK);
    }

    @GetMapping("/ticket1")
    public ResponseEntity<?> getTicket1(){
        return new ResponseEntity<>(ticket1Repository.findAll().size(), HttpStatus.OK);
    }


    @GetMapping("/ticketByDate")
    public ResponseEntity<?> getTicketByDate(){
        LocalDate date = LocalDate.of(2025, 4, 25);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999_999_000);

        System.out.println(startOfDay + " " + endOfDay);
        long ticketCount = ticketRepository.countTicketsCreatedBetween(startOfDay, endOfDay);

        return new ResponseEntity<>(ticketCount, HttpStatus.OK);
    }

    @GetMapping("/ticket1ByDate")
    public ResponseEntity<?> getTicket1ByDate(){
        LocalDate date = LocalDate.of(2025, 4, 25);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999_999_000);
        System.out.println("start " + startOfDay + ", end " + endOfDay);
        return new ResponseEntity<>(ticket1Repository.countTicketsCreatedBetween(startOfDay, endOfDay), HttpStatus.OK);
    }

}
