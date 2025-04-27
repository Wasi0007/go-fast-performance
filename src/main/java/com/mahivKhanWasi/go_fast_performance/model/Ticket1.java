package com.mahivKhanWasi.go_fast_performance.model;

import com.mahivKhanWasi.go_fast_performance.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(
        name = "ticket1",
        indexes = {
                @Index(name = "idx_created_at", columnList = "createdAt"),
                @Index(name = "idx_created_by", columnList = "createdBy"),
                @Index(name = "idx_source_id", columnList = "source_id")  // Indexing by source_id
        }
)
public class Ticket1 extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private double penalty;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", referencedColumnName = "id")
    private Station source;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_id", referencedColumnName = "id")
    private Station destination;

    private Double cost;

}
