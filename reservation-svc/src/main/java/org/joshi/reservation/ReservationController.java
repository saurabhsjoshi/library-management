package org.joshi.reservation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Value("${inventory-svc-uri:http://inventory-svc:8082}/api/inventory/")
    private String inventoryApi;

    private record BookReservationReq(String reservationId, String bookName, String quantity, String numberOfDays) {
    }

    private final RestTemplate restTemplate;

    private final ReservationRepository repository;

    @Autowired
    public ReservationController(ReservationRepository reservationRepository, RestTemplateBuilder builder) {
        this.repository = reservationRepository;
        this.restTemplate = builder
                .errorHandler(new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) {
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) {
                        // Absorb error
                    }
                })
                .build();
    }

    @PostMapping("/")
    public ResponseEntity<BookReservation> createReservation(@RequestHeader(value = "username") String username,
                                                             @RequestHeader(value = "password") String password,
                                                             @RequestBody BookReservationReq req) {
        BookReservation reservation = new BookReservation();
        reservation.setReservationId(req.reservationId);
        reservation.setBookName(req.bookName);
        reservation.setUsername(username);
        reservation.setQuantity(Integer.valueOf(req.quantity));
        reservation.setNumberOfDays(Integer.valueOf(req.numberOfDays));

        var existing = repository.findByReservationIdAndUsername(req.reservationId, username);

        // If present ignore since reservations are immutable
        if (existing.isPresent()) {
            return new ResponseEntity<>(existing.get(), HttpStatus.CREATED);
        }

        var quantity = getQuantity(req.bookName);

        // Book not found
        if (quantity == -1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Not enough remaining
        if (quantity < reservation.quantity) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        var savedReservation = repository.save(reservation);
        removeQuantity(req.bookName, reservation.quantity, username, password);

        return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<BookReservation> getById(@RequestHeader(value = "username") String username,
                                                   @PathVariable String reservationId) {
        Optional<BookReservation> reservation;

        // Admin can access everything
        if (username.equals("admin")) {
            reservation = repository.findById(reservationId);
        } else {
            reservation = repository.findByReservationIdAndUsername(reservationId, username);
        }

        return reservation
                .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteById(@RequestHeader(value = "username") String username,
                                           @RequestHeader(value = "password") String password,
                                           @PathVariable String reservationId) {
        var reservationOpt = repository.findByReservationIdAndUsername(reservationId, username);
        if (reservationOpt.isPresent()) {
            var reservation = reservationOpt.get();
            repository.deleteById(reservationId);
            addQuantity(reservation.bookName, reservation.quantity, username, password);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    public int getQuantity(String bookName) {
        var resp = restTemplate.getForEntity(inventoryApi + bookName, String.class);

        if (resp.getStatusCode() != HttpStatus.OK || !StringUtils.hasText(resp.getBody())) {
            return -1;
        }

        var body = JsonParser.parseString(resp.getBody()).getAsJsonObject();
        return body.get("quantity").getAsInt();
    }

    public void removeQuantity(String bookName, int quantity, String username, String password) {
        HttpEntity<String> req = updateQuantity(quantity, username, password);
        restTemplate.postForEntity(inventoryApi + bookName + "/remove", req, String.class);
    }

    public void addQuantity(String bookName, int quantity, String username, String password) {
        var req = updateQuantity(quantity, username, password);
        restTemplate.postForEntity(inventoryApi + bookName + "/add", req, String.class);
    }

    private HttpEntity<String> updateQuantity(int quantity, String username, String password) {
        JsonObject object = new JsonObject();
        object.add("quantity", new JsonPrimitive(quantity));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("username", username);
        headers.add("password", password);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(object.toString(), headers);
    }
}
