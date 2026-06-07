package org.Library.book;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {

    List<Author> getAuthors();
    void addAuthor(Author author);
    void saveAuthors();
    Long getNextAuthorId();
    Optional<Author> findAuthorById(Long id);
}
