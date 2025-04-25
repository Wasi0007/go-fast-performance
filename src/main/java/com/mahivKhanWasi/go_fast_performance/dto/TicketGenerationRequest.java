package com.mahivKhanWasi.go_fast_performance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketGenerationRequest {
    private LocalDate date;

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
