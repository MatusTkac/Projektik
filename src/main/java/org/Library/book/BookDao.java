package org.Library.book;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    List<Book> getBooks();

    void addBook(Book book);

    void saveBooks();

    Long getNextBookId();

    Optional<Book> findBookById(Long id);
}