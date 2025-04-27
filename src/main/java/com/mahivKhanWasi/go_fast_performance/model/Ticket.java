package com.mahivKhanWasi.go_fast_performance.model;
import com.mahivKhanWasi.go_fast_performance.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Entity
@Table(
        name = "ticket",
        indexes = {
                @Index(name = "idx_created_at", columnList = "createdAt"),
                @Index(name = "idx_created_by", columnList = "createdBy"),
                @Index(name = "idx_trip_id", columnList = "trip_id")  // Indexing by trip_id
        }
)
public class Ticket extends BaseEntity {

    private double penalty;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", referencedColumnName = "id")
    private Trip trip;

}
