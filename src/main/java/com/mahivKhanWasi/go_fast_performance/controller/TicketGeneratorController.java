package com.mahivKhanWasi.go_fast_performance.controller;

import com.mahivKhanWasi.go_fast_performance.dto.TicketGenerationRequest;
import com.mahivKhanWasi.go_fast_performance.enums.TicketStatus;
import com.mahivKhanWasi.go_fast_performance.model.Ticket;
import com.mahivKhanWasi.go_fast_performance.model.Ticket1;
import com.mahivKhanWasi.go_fast_performance.model.Trip;
import com.mahivKhanWasi.go_fast_performance.repository.Ticket1Repository;
import com.mahivKhanWasi.go_fast_performance.repository.TicketRepository;
import com.mahivKhanWasi.go_fast_performance.repository.TripRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketGeneratorController {

    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;
    private final Ticket1Repository ticket1Repository;
    private final JdbcTemplate jdbcTemplate; // For using direct SQL inserts
    private final Random random = new Random();
    private final EntityManager entityManager;

    @PostMapping("/generate")
    @Transactional
    public String generateTickets(@RequestBody TicketGenerationRequest request) {
        long startTime = System.currentTimeMillis(); // Start timer

        List<Trip> trips = tripRepository.findAll();

        if (trips.isEmpty()) {
            return "No trips found in the database!";
        }

        int totalTickets = 200_000; // Adjust the total tickets to generate
        List<Object[]> batchArgs = new ArrayList<>(100_000); // Batch size of 100_000

        for (int i = 0; i < totalTickets; i++) {
            Trip randomTrip = trips.get(random.nextInt(trips.size()));

            // Prepare data for batch insert
            batchArgs.add(new Object[]{
                    0.0, // penalty
                    TicketStatus.BOUGHT.name(), // status
                    randomTrip.getId(), // trip_id
                    request.getDate().atStartOfDay(), // createdAt
                    1L, // createdBy (defaulted to 1 for now)
                    1L  // updatedBy (defaulted to 1 for now)
            });

            // Insert batch every 100_000 records
            if (batchArgs.size() >= 100_000) {
                String sql = "INSERT INTO ticket (penalty, status, trip_id, created_at, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?)";
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear(); // Clear after batch insert
            }
        }

        // Insert remaining tickets if any
        if (!batchArgs.isEmpty()) {
            String sql = "INSERT INTO ticket (penalty, status, trip_id, created_at, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }

        long endTime = System.currentTimeMillis(); // End timer
        long durationInSeconds = (endTime - startTime) / 1000;

        return "Successfully generated " + totalTickets + " tickets in " + durationInSeconds + " seconds!";
    }

    @GetMapping("/migrate")
    @Transactional
    public String migrateTickets() {
        long startTime = System.nanoTime();

        LocalDate startDate = LocalDate.of(2025, 4, 25);
        LocalDate endDate = LocalDate.of(2026, 5, 2);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59, 999_999_000);

            List<Ticket> ticketList = ticketRepository.findAllByCreatedAtBetween(startOfDay, endOfDay);

            if (ticketList.isEmpty()) {
                continue;
            }

            insertTicketsBatch(ticketList, startTime, date);

            entityManager.clear(); // 🧹 CLEAR persistence context after processing 1 day
            System.gc(); // optional: manually hint garbage collector
        }

        return "Successfully migrated";
    }


    public void insertTicketsBatch(List<Ticket> ticketList, long startTime, LocalDate date) {
        long batchStartTime = System.nanoTime();

        String sql = "INSERT INTO ticket1 (status, penalty, source_id, destination_id, cost, created_at, created_by, updated_at, updated_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> batchArgs = ticketList.stream().map(ticket -> new Object[]{
                ticket.getStatus().toString(),          // Assuming status is Enum
                ticket.getPenalty(),
                ticket.getTrip().getSource().getId(),    // Assuming Station has getId()
                ticket.getTrip().getDestination().getId(),
                ticket.getTrip().getCost(),
                ticket.getCreatedAt(),
                ticket.getCreatedBy(),
                ticket.getUpdatedAt(),
                ticket.getUpdatedBy()
        }).collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);

        long batchEndTime = System.nanoTime();
        long batchDuration = batchEndTime - batchStartTime;
        Duration duration = Duration.ofNanos(batchDuration);

        long totalTimeElapsed = System.nanoTime() - startTime;
        Duration totalDuration = Duration.ofNanos(totalTimeElapsed);

        System.out.println("Date done " + date);
        System.out.println("Batch time taken: " + duration.toMillis() + " ms.");
        System.out.println("Total time elapsed so far: " + totalDuration.toMillis() + " ms.");
    }
}