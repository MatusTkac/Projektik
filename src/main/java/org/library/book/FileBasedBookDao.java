package org.library.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBasedBookDao implements BookDao {
    private final Path booksFile;
    private final AuthorDao authorDao;
    private final CategoryDao categoryDao;
    private final List<Book> books = new ArrayList<>();

    public FileBasedBookDao(Path booksFile, AuthorDao authorDao, CategoryDao categoryDao) {
        this.booksFile = booksFile;
        this.authorDao = authorDao;
        this.categoryDao = categoryDao;

        createFileIfMissing();
        loadBooks();
    }

    @Override
    public List<Book> getBooks() {
        return books;
    }

    @Override
    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    @Override
    public void saveBooks() {
        List<String> lines = books.stream()
                .map(book -> book.getId() + "|" +
                        book.getTitle() + "|" +
                        book.getPublishYear() + "|" +
                        getAuthorId(book) + "|" +
                        getCategoryId(book) + "|" +
                        book.isAvailable())
                .toList();

        writeLines(lines);
    }

    @Override
    public Long getNextBookId() {
        return books.stream()
                .map(Book::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    private void loadBooks() {
        readLines().forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 6) {
                Long authorId = Long.parseLong(parts[3]);
                Long categoryId = Long.parseLong(parts[4]);

                Author author = authorDao.findAuthorById(authorId).orElse(null);
                Category category = categoryDao.findCategoryById(categoryId).orElse(null);

                books.add(new Book(
                        Long.parseLong(parts[0]),
                        parts[1],
                        Integer.parseInt(parts[2]),
                        author,
                        category,
                        Boolean.parseBoolean(parts[5])
                ));
            }
        });
    }

    private Long getAuthorId(Book book) {
        return book.getAuthor() == null ? 0L : book.getAuthor().getId();
    }

    private Long getCategoryId(Book book) {
        return book.getCategory() == null ? 0L : book.getCategory().getId();
    }

    private void createFileIfMissing() {
        try {
            if (booksFile.getParent() != null) {
                Files.createDirectories(booksFile.getParent());
            }

            if (!Files.exists(booksFile)) {
                Files.createFile(booksFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create books file: " + booksFile, e);
        }
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(booksFile)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read books file: " + booksFile, e);
        }
    }

    private void writeLines(List<String> lines) {
        try {
            Files.write(
                    booksFile,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write books file: " + booksFile, e);
        }
    }
}