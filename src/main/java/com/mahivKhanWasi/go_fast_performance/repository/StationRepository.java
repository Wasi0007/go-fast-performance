package com.mahivKhanWasi.go_fast_performance.repository;

import com.mahivKhanWasi.go_fast_performance.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByName(String name);
}
