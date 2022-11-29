package org.joshi.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Class that represents a user in database.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
public class LibraryUser implements Serializable {
    @Id
    private String username;
    private String password;

    private String displayName;
}
