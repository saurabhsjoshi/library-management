package org.joshi.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
