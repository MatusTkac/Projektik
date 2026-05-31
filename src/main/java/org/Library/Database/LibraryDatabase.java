package org.Library.Database;

import org.Library.Book.Author;
import org.Library.Book.Book;
import org.Library.Book.Category;
import org.Library.Loan.Loan;
import org.Library.User.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibraryDatabase {
    private static final String BOOK_FOLDER = "src/main/java/org/Library/Book/";
    private static final String USER_FOLDER = "src/main/java/org/Library/User/";
    private static final String LOAN_FOLDER = "src/main/java/org/Library/Loan/";

    private static final Path AUTHORS_FILE = Path.of(BOOK_FOLDER + "Authors.txt");
    private static final Path CATEGORIES_FILE = Path.of(BOOK_FOLDER + "Categories.txt");
    private static final Path BOOKS_FILE = Path.of(BOOK_FOLDER + "Books.txt");
    private static final Path USERS_FILE = Path.of(USER_FOLDER + "Users.txt");
    private static final Path LOANS_FILE = Path.of(LOAN_FOLDER + "Loans.txt");

    private final List<Author> authors = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    public LibraryDatabase() {
        createDatabaseFiles();
        loadAll();
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void addAuthor(Author author) {
        authors.add(author);
        saveAuthors();
    }

    public void addCategory(Category category) {
        categories.add(category);
        saveCategories();
    }

    public void addBook(Book book) {
        books.add(book);
        saveBooks();
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
        saveLoans();
        saveBooks();
    }

    public void updateLoanReturn(Loan loan) {
        saveLoans();
        saveBooks();
    }

    public void saveAll() {
        saveAuthors();
        saveCategories();
        saveBooks();
        saveUsers();
        saveLoans();
    }

    public Long getNextAuthorId() {
        return getNextId(authors.stream().map(Author::getId).toList());
    }

    public Long getNextCategoryId() {
        return getNextId(categories.stream().map(Category::getId).toList());
    }

    public Long getNextBookId() {
        return getNextId(books.stream().map(Book::getId).toList());
    }

    public Long getNextUserId() {
        return getNextId(users.stream().map(User::getId).toList());
    }

    public Long getNextLoanId() {
        return getNextId(loans.stream().map(Loan::getId).toList());
    }

    public Optional<Author> findAuthorById(Long id) {
        return authors.stream()
                .filter(author -> author.getId().equals(id))
                .findFirst();
    }

    public Optional<Category> findCategoryById(Long id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst();
    }

    public Optional<Book> findBookById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    private Long getNextId(List<Long> ids) {
        return ids.stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private void createDatabaseFiles() {
        try {
            Files.createDirectories(Path.of(BOOK_FOLDER));
            Files.createDirectories(Path.of(USER_FOLDER));
            Files.createDirectories(Path.of(LOAN_FOLDER));

            createFileIfMissing(AUTHORS_FILE);
            createFileIfMissing(CATEGORIES_FILE);
            createFileIfMissing(BOOKS_FILE);
            createFileIfMissing(USERS_FILE);
            createFileIfMissing(LOANS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Could not create database files.", e);
        }
    }

    private void createFileIfMissing(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    private void loadAll() {
        loadAuthors();
        loadCategories();
        loadUsers();
        loadBooks();
        loadLoans();
    }

    private void loadAuthors() {
        readLines(AUTHORS_FILE).forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 3) {
                authors.add(new Author(
                        Long.parseLong(parts[0]),
                        parts[1],
                        parts[2]
                ));
            }
        });
    }

    private void loadCategories() {
        readLines(CATEGORIES_FILE).forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 2) {
                categories.add(new Category(
                        Long.parseLong(parts[0]),
                        parts[1]
                ));
            }
        });
    }

    private void loadUsers() {
        readLines(USERS_FILE).forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 4) {
                users.add(new User(
                        Long.parseLong(parts[0]),
                        parts[1],
                        parts[2],
                        LocalDate.parse(parts[3])
                ));
            }
        });
    }

    private void loadBooks() {
        readLines(BOOKS_FILE).forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 6) {
                Long authorId = Long.parseLong(parts[3]);
                Long categoryId = Long.parseLong(parts[4]);

                Author author = findAuthorById(authorId).orElse(null);
                Category category = findCategoryById(categoryId).orElse(null);

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

    private void loadLoans() {
        readLines(LOANS_FILE).forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 6) {
                Book book = findBookById(Long.parseLong(parts[1])).orElse(null);
                User user = findUserById(Long.parseLong(parts[2])).orElse(null);

                Loan loan = new Loan();
                loan.setId(Long.parseLong(parts[0]));
                loan.setBook(book);
                loan.setUser(user);
                loan.setBorrowedAt(LocalDate.parse(parts[3]));
                loan.setReturnDate(LocalDate.parse(parts[4]));
                loan.setReturned(Boolean.parseBoolean(parts[5]));

                loans.add(loan);
            }
        });
    }

    private List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + path, e);
        }
    }

    private void saveAuthors() {
        List<String> lines = authors.stream()
                .map(author -> author.getId() + "|" + author.getFirstName() + "|" + author.getLastName())
                .toList();

        writeLines(AUTHORS_FILE, lines);
    }

    private void saveCategories() {
        List<String> lines = categories.stream()
                .map(category -> category.getId() + "|" + category.getName())
                .toList();

        writeLines(CATEGORIES_FILE, lines);
    }

    private void saveBooks() {
        List<String> lines = books.stream()
                .map(book -> book.getId() + "|" +
                        book.getTitle() + "|" +
                        book.getPublishYear() + "|" +
                        getAuthorId(book) + "|" +
                        getCategoryId(book) + "|" +
                        book.isAvailable())
                .toList();

        writeLines(BOOKS_FILE, lines);
    }

    private void saveUsers() {
        List<String> lines = users.stream()
                .map(user -> user.getId() + "|" +
                        user.getName() + "|" +
                        user.getEmail() + "|" +
                        user.getRegisteredAt())
                .toList();

        writeLines(USERS_FILE, lines);
    }

    private void saveLoans() {
        List<String> lines = loans.stream()
                .map(loan -> loan.getId() + "|" +
                        getBookId(loan) + "|" +
                        getUserId(loan) + "|" +
                        loan.getBorrowedAt() + "|" +
                        loan.getReturnDate() + "|" +
                        loan.isReturned())
                .toList();

        writeLines(LOANS_FILE, lines);
    }

    private Long getAuthorId(Book book) {
        return book.getAuthor() == null ? 0L : book.getAuthor().getId();
    }

    private Long getCategoryId(Book book) {
        return book.getCategory() == null ? 0L : book.getCategory().getId();
    }

    private Long getBookId(Loan loan) {
        return loan.getBook() == null ? 0L : loan.getBook().getId();
    }

    private Long getUserId(Loan loan) {
        return loan.getUser() == null ? 0L : loan.getUser().getId();
    }

    private void writeLines(Path path, List<String> lines) {
        try {
            Files.write(
                    path,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write file: " + path, e);
        }
    }
}
