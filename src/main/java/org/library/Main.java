package org.library;

import org.library.book.Author;
import org.library.book.Book;
import org.library.book.Category;
import org.library.database.LibraryDatabase;
import org.library.history.HistoryService;
import org.library.loan.Loan;
import org.library.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryDatabase database = new LibraryDatabase();
    private static final HistoryService historyService = new HistoryService("src/main/java/org/Library/History.txt");

    public static void main(String[] args) {
        System.out.println("Welcome to Library System!");

        boolean running = true;

        while (running) {
            printMainMenu();

            int choice = readInt("Choose option: ");

            switch (choice) {
                case 1 -> adminMenu();
                case 2 -> userMenu();
                case 0 -> {
                    database.saveAll();
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("===== MAIN MENU =====");
        System.out.println("1. Admin");
        System.out.println("2. User");
        System.out.println("0. Exit");
    }

    private static void adminMenu() {
        boolean adminRunning = true;

        while (adminRunning) {
            System.out.println();
            System.out.println("===== ADMIN MENU =====");
            System.out.println("1. Add Author");
            System.out.println("2. Add Category");
            System.out.println("3. Add Book");
            System.out.println("4. Add User");
            System.out.println("5. Show All Authors");
            System.out.println("6. Show All Categories");
            System.out.println("7. Show All Books");
            System.out.println("8. Show All Users");
            System.out.println("0. Back");

            int choice = readInt("Choose option: ");

            switch (choice) {
                case 1 -> addAuthor();
                case 2 -> addCategory();
                case 3 -> addBook();
                case 4 -> addUser();
                case 5 -> showAuthors();
                case 6 -> showCategories();
                case 7 -> showBooks(database.getBooks());
                case 8 -> showUsers();
                case 0 -> adminRunning = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void userMenu() {
        boolean userRunning = true;

        while (userRunning) {
            System.out.println();
            System.out.println("===== USER MENU =====");
            System.out.println("1. Show All Books");
            System.out.println("2. Show Available Books");
            System.out.println("3. Show Unavailable Books");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("0. Back");

            int choice = readInt("Choose option: ");

            switch (choice) {
                case 1 -> showBooks(database.getBooks());
                case 2 -> showBooks(getAvailableBooks());
                case 3 -> showBooks(getUnavailableBooks());
                case 4 -> borrowBook();
                case 5 -> returnBook();
                case 0 -> userRunning = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addAuthor() {
        System.out.println();
        System.out.println("===== ADD AUTHOR =====");

        String firstName = readText("First name: ");
        String lastName = readText("Last name: ");

        Author author = new Author(
                database.getNextAuthorId(),
                firstName,
                lastName
        );

        database.addAuthor(author);

        System.out.println("Author added successfully.");
    }

    private static void addCategory() {
        System.out.println();
        System.out.println("===== ADD CATEGORY =====");

        String name = readText("Category name: ");

        Category category = new Category(
                database.getNextCategoryId(),
                name
        );

        database.addCategory(category);

        System.out.println("Category added successfully.");
    }

    private static void addBook() {
        System.out.println();
        System.out.println("===== ADD BOOK =====");

        if (database.getAuthors().isEmpty()) {
            System.out.println("You need to add at least one author before adding a book.");
            return;
        }

        if (database.getCategories().isEmpty()) {
            System.out.println("You need to add at least one category before adding a book.");
            return;
        }

        String title = readText("Book title: ");
        int publishYear = readInt("Publish year: ");

        showAuthors();
        Long authorId = readLong("Choose author id: ");

        Optional<Author> author = database.findAuthorById(authorId);

        if (author.isEmpty()) {
            System.out.println("Author not found.");
            return;
        }

        showCategories();
        Long categoryId = readLong("Choose category id: ");

        Optional<Category> category = database.findCategoryById(categoryId);

        if (category.isEmpty()) {
            System.out.println("Category not found.");
            return;
        }

        Book book = new Book(
                database.getNextBookId(),
                title,
                publishYear,
                author.get(),
                category.get(),
                true
        );

        database.addBook(book);

        System.out.println("Book added successfully.");
    }


    private static void addUser() {
        System.out.println();
        System.out.println("===== ADD USER =====");

        String name = readText("Name: ");
        String email = readText("Email: ");

        User user = new User(
                database.getNextUserId(),
                name,
                email,
                LocalDate.now()
        );

        database.addUser(user);

        System.out.println("User added successfully.");
    }

    private static void borrowBook() {
        System.out.println();
        System.out.println("===== BORROW BOOK =====");

        if (database.getUsers().isEmpty()) {
            System.out.println("No users found. Ask admin to create a user first.");
            return;
        }

        List<Book> availableBooks = getAvailableBooks();

        if (availableBooks.isEmpty()) {
            System.out.println("No available books right now.");
            return;
        }

        showUsers();
        Long userId = readLong("Enter your user id: ");

        Optional<User> user = database.findUserById(userId);

        if (user.isEmpty()) {
            System.out.println("User not found.");
            return;
        }

        showBooks(availableBooks);
        Long bookId = readLong("Choose book id to borrow: ");

        Optional<Book> book = database.findBookById(bookId);

        if (book.isEmpty()) {
            System.out.println("Book not found.");
            return;
        }

        if (!book.get().isAvailable()) {
            System.out.println("This book is not available.");
            return;
        }

        Loan loan = new Loan(
                database.getNextLoanId(),
                book.get(),
                user.get(),
                LocalDate.now(),
                LocalDate.now().plusDays(14)
        );

        database.addLoan(loan);
        historyService.saveLoanCreated(loan);

        System.out.println("Book borrowed successfully.");
        System.out.println("Please return it by: " + loan.getReturnDate());
    }

    private static void returnBook() {
        System.out.println();
        System.out.println("===== RETURN BOOK =====");

        if (database.getUsers().isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        showUsers();
        Long userId = readLong("Enter your user id: ");

        Optional<User> user = database.findUserById(userId);

        if (user.isEmpty()) {
            System.out.println("User not found.");
            return;
        }

        List<Loan> activeLoans = database.getLoans()
                .stream()
                .filter(loan -> loan.getUser() != null)
                .filter(loan -> loan.getUser().getId().equals(userId))
                .filter(loan -> !loan.isReturned())
                .toList();

        if (activeLoans.isEmpty()) {
            System.out.println("This user has no borrowed books to return.");
            return;
        }

        System.out.println();
        System.out.println("===== YOUR BORROWED BOOKS =====");

        activeLoans.forEach(loan -> {
            String bookTitle = loan.getBook() == null ? "Unknown Book" : loan.getBook().getTitle();

            System.out.println(loan.getId() + ". " +
                    bookTitle +
                    " | Borrowed at: " + loan.getBorrowedAt() +
                    " | Return date: " + loan.getReturnDate());
        });

        Long loanId = readLong("Choose loan id to return: ");

        Optional<Loan> selectedLoan = activeLoans.stream()
                .filter(loan -> loan.getId().equals(loanId))
                .findFirst();

        if (selectedLoan.isEmpty()) {
            System.out.println("Loan not found in this user's borrowed books.");
            return;
        }

        selectedLoan.get().markReturned();
        database.updateLoanReturn(selectedLoan.get());
        historyService.saveLoanReturned(selectedLoan.get());

        System.out.println("Book returned successfully.");
    }

    private static List<Book> getAvailableBooks() {
        return database.getBooks()
                .stream()
                .filter(Book::isAvailable)
                .toList();
    }

    private static List<Book> getUnavailableBooks() {
        return database.getBooks()
                .stream()
                .filter(book -> !book.isAvailable())
                .toList();
    }

    private static void showAuthors() {
        System.out.println();
        System.out.println("===== AUTHORS =====");

        if (database.getAuthors().isEmpty()) {
            System.out.println("No authors found.");
            return;
        }

        database.getAuthors().forEach(author ->
                System.out.println(author.getId() + ". " + author.getFullName())
        );
    }

    private static void showCategories() {
        System.out.println();
        System.out.println("===== CATEGORIES =====");

        if (database.getCategories().isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        database.getCategories().forEach(category ->
                System.out.println(category.getId() + ". " + category.getName())
        );
    }

    private static void showBooks(List<Book> books) {
        System.out.println();
        System.out.println("===== BOOKS =====");

        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        books.forEach(book -> {
            String authorName = book.getAuthor() == null ? "Unknown Author" : book.getAuthor().getFullName();
            String categoryName = book.getCategory() == null ? "Uncategorized" : book.getCategory().getName();
            String status = book.isAvailable() ? "Available" : "Unavailable";

            System.out.println(book.getId() + ". " +
                    book.getTitle() +
                    " | Author: " + authorName +
                    " | Category: " + categoryName +
                    " | Year: " + book.getPublishYear() +
                    " | Status: " + status);
        });
    }

    private static void showUsers() {
        System.out.println();
        System.out.println("===== USERS =====");

        if (database.getUsers().isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        database.getUsers().forEach(user ->
                System.out.println(user.getId() + ". " + user.getName() + " | " + user.getEmail())
        );
    }

    private static String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static Long readLong(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid id number.");
            }
        }
    }
}