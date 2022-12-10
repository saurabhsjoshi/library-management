package org.joshi.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class LibraryBook {
    @Id
    private String bookName;

    private String author;

    private Integer publishYear;

    private Integer quantity;
}
