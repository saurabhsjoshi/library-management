package org.joshi.inventory;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookRepository extends CrudRepository<LibraryBook, String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE library_book b SET b.quantity = b.quantity + :quantity WHERE b.book_name = :bookName", nativeQuery = true)
    void incrementQuantityByName(String bookName, int quantity);

    @Transactional
    @Modifying
    @Query(value = "UPDATE library_book b SET b.quantity = b.quantity - :quantity WHERE b.book_name = :bookName", nativeQuery = true)
    void decrementQuantityByName(String bookName, int quantity);
}
