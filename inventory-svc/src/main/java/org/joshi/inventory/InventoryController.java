package org.joshi.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private record Quantity(int quantity) {
    }


    private final BookRepository bookRepository;

    @Autowired
    public InventoryController(BookRepository repository) {
        this.bookRepository = repository;
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<LibraryBook> getBooks() {
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                .toList();
    }

    @GetMapping("/{bookName}")
    public ResponseEntity<LibraryBook> getBook(@PathVariable String bookName) {
        return bookRepository.findById(bookName)
                .map(b -> new ResponseEntity<>(b, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryBook createBook(@RequestBody LibraryBook book) {
        return bookRepository.save(book);
    }


    @DeleteMapping("/{bookName}")
    public void removeBook(@PathVariable String bookName) {
        bookRepository.deleteById(bookName);
    }

    @PostMapping("/{bookName}/add")
    public LibraryBook addBook(@PathVariable String bookName, @RequestBody Quantity quantity) {
        bookRepository.incrementQuantityByName(bookName, quantity.quantity);
        return bookRepository.findById(bookName)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @PostMapping("/{bookName}/remove")
    public LibraryBook removeBook(@PathVariable String bookName, @RequestBody Quantity quantity) {
        bookRepository.decrementQuantityByName(bookName, quantity.quantity);
        return bookRepository.findById(bookName)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

}
