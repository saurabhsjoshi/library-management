package org.joshi.reservation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends CrudRepository<BookReservation, String> {
    Optional<BookReservation> findByReservationIdAndUsername(String reservationId, String username);
}
