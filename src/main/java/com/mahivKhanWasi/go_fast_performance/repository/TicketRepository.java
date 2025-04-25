package com.mahivKhanWasi.go_fast_performance.repository;

import com.mahivKhanWasi.go_fast_performance.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
