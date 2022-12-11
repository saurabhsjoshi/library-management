package org.joshi.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final RestTemplate restTemplate;

    private final ReservationRepository repository;

    @Autowired
    public ReservationController(ReservationRepository reservationRepository, RestTemplateBuilder builder) {
        this.repository = reservationRepository;
        this.restTemplate = builder.build();
    }

    @PostMapping("/")
    public BookReservation createReservation(@RequestBody BookReservation reservation) {
        return repository.save(reservation);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<BookReservation> getById(@PathVariable String reservationId) {
        return repository.findById(reservationId)
                .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
