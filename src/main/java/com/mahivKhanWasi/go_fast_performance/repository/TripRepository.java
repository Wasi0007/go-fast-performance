package com.mahivKhanWasi.go_fast_performance.repository;

import com.mahivKhanWasi.go_fast_performance.model.Station;
import com.mahivKhanWasi.go_fast_performance.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
    boolean existsBySourceAndDestination(Station source, Station destination);
}