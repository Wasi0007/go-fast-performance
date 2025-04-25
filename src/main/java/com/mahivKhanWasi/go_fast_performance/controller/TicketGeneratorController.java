package com.mahivKhanWasi.go_fast_performance.controller;

import com.mahivKhanWasi.go_fast_performance.dto.TicketGenerationRequest;
import com.mahivKhanWasi.go_fast_performance.enums.TicketStatus;
import com.mahivKhanWasi.go_fast_performance.model.Ticket;
import com.mahivKhanWasi.go_fast_performance.model.Trip;
import com.mahivKhanWasi.go_fast_performance.repository.TicketRepository;
import com.mahivKhanWasi.go_fast_performance.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketGeneratorController {

    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;
    private final JdbcTemplate jdbcTemplate; // For using direct SQL inserts
    private final Random random = new Random();

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
}