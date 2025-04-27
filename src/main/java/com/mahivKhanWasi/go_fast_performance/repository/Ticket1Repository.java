package com.mahivKhanWasi.go_fast_performance.repository;

import com.mahivKhanWasi.go_fast_performance.model.Ticket;
import com.mahivKhanWasi.go_fast_performance.model.Ticket1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface Ticket1Repository extends JpaRepository<Ticket1, Long> {

    List<Ticket1> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM Ticket1 t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    long countTicketsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
