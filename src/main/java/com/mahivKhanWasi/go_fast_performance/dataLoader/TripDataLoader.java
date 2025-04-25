package com.mahivKhanWasi.go_fast_performance.dataLoader;

import com.mahivKhanWasi.go_fast_performance.model.Station;
import com.mahivKhanWasi.go_fast_performance.model.Trip;
import com.mahivKhanWasi.go_fast_performance.repository.StationRepository;
import com.mahivKhanWasi.go_fast_performance.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
public class TripDataLoader implements CommandLineRunner {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private TripRepository tripRepository;

    @Override
    public void run(String... args) {
        List<Station> stations = stationRepository.findAll();

        for (int i = 0; i < stations.size(); i++) {
            for (int j = 0; j < stations.size(); j++) {
                if (i != j) {
                    Station source = stations.get(i);
                    Station destination = stations.get(j);

                    boolean tripExists = tripRepository.existsBySourceAndDestination(source, destination);
                    if (!tripExists) {
                        double cost = 10.0 * Math.abs(i - j);
                        Trip trip1 = new Trip();
                        trip1.setSource(source);
                        trip1.setDestination(destination);
                        trip1.setCost(cost);
                        tripRepository.save(trip1);

                        Trip trip2 = new Trip();
                        trip2.setSource(destination);
                        trip2.setDestination(source);
                        trip2.setCost(cost);
                        tripRepository.save(trip2);
                    }
                }
            }
        }
    }
}
