package com.mahivKhanWasi.go_fast_performance.dataLoader;

import com.mahivKhanWasi.go_fast_performance.model.Ticket;
import com.mahivKhanWasi.go_fast_performance.model.Ticket1;
import com.mahivKhanWasi.go_fast_performance.repository.TicketRepository;
import com.mahivKhanWasi.go_fast_performance.repository.Ticket1Repository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(5)
public class TicketDataMigrationRunner implements CommandLineRunner {

    private static final int CHUNK_SIZE = 10000; // Size of each chunk to fetch and insert

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private Ticket1Repository ticket1Repository;

    @Override
    public void run(String... args) throws Exception {
        long startTime = System.nanoTime(); // Start measuring total time

        long totalRecords = ticketRepository.count(); // Count the total records in ticket table
        int totalPages = (int) Math.ceil((double) totalRecords / CHUNK_SIZE); // Calculate total pages

        for (int page = 0; page < totalPages; page++) {
            Pageable pageable = PageRequest.of(page, CHUNK_SIZE); // Define the page to fetch
            List<Ticket> ticketList = ticketRepository.findAll(pageable).getContent();

            if (ticketList.isEmpty()) {
                break;
            }

            // Pass chunk index and total time to the insert function
            insertTicketsBatch(ticketList, page + 1, totalPages, startTime); // chunkIndex starts from 1
        }
    }

    @Transactional
    public void insertTicketsBatch(List<Ticket> ticketList, int chunkIndex, int total, long startTime) {
        // Start measuring the time for the current batch processing
        long batchStartTime = System.nanoTime();

        // Convert the list of Ticket objects to a list of Ticket1 objects
        List<Ticket1> ticket1List = ticketList.stream().map(ticket -> {
            return Ticket1.builder()
                    .status(ticket.getStatus())
                    .penalty(ticket.getPenalty())
                    .source(ticket.getTrip().getSource())  // Assuming Trip has a source and destination
                    .destination(ticket.getTrip().getDestination()) // Assuming Trip has a source and destination
                    .cost(ticket.getTrip().getCost())
                    .createdAt(ticket.getCreatedAt())
                    .createdBy(ticket.getCreatedBy())
                    .updatedAt(ticket.getUpdatedAt())
                    .updatedBy(ticket.getUpdatedBy())
                    .build();
        }).collect(Collectors.toList()); // Collecting the mapped objects into a list

        // Batch insert Ticket1 objects into the ticket1 table
        ticket1Repository.saveAll(ticket1List);

        // Calculate the total time for the current batch process
        long batchEndTime = System.nanoTime();
        long batchDuration = batchEndTime - batchStartTime;  // Time taken for this batch in nanoseconds
        Duration duration = Duration.ofNanos(batchDuration);
        System.out.println("Processed chunk " + chunkIndex + " with " + ticket1List.size() + " records. Remaining " + (total - chunkIndex));

        // Calculate the total time elapsed so far
        long totalTimeElapsed = System.nanoTime() - startTime;
        Duration totalDuration = Duration.ofNanos(totalTimeElapsed);

        // Print the time taken for the batch and total time elapsed
        System.out.println("Batch " + chunkIndex + " took " + duration.toMillis() + " ms.");
        System.out.println("Total time elapsed so far: " + totalDuration.toMillis() + " ms.");
    }
}