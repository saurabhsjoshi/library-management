package org.joshi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository repository;

    private record AuthRequest(String username, String password) {
    }

    @Autowired
    public AuthController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Void> authorize(@RequestBody AuthRequest request) {
        var user = repository.findById(request.username);

        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (user.get().getPassword().equals(request.password)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
