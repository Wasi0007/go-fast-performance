package com.mahivKhanWasi.go_fast_performance.repository;

import com.mahivKhanWasi.go_fast_performance.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM ticket WHERE created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    long countTicketsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


}
