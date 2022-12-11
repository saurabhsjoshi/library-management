package org.joshi.reservation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class BookReservation {
    @Id
    public String reservationId;
    public String bookName;
    public String username;
    public Integer quantity;
    public Integer numberOfDays;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;
}
