package dev.anton_kulakov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Locations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_latitude_longitude",
                        columnNames = {"userID", "latitude", "longitude"}
                )})
@Getter
@Setter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int userID;

    @Column(unique = true)
    private BigDecimal latitude;

    @Column(unique = true)
    private BigDecimal longitude;
}
