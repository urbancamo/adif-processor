package uk.m0nom.adifproc.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@RequiredArgsConstructor
@Data
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String callsign;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp timestamp;

    public Log(String callsign) {
        this.callsign = callsign;
    }
}
