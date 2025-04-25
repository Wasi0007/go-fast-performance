package com.mahivKhanWasi.go_fast_performance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "trip")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", referencedColumnName = "id")
    private Station source;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_id", referencedColumnName = "id")
    private Station destination;

    @Column(nullable = false)
    private Double cost;
}