package org.Library.database;

import org.Library.book.*;
import org.Library.loan.FileBasedLoanDao;
import org.Library.loan.Loan;
import org.Library.loan.LoanDao;
import org.Library.user.FileBasedUserDao;
import org.Library.user.User;
import org.Library.user.UserDao;

import java.nio.file.Path;
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

    private final AuthorDao authorDao;
    private final CategoryDao categoryDao;
    private final BookDao bookDao;
    private final UserDao userDao;
    private final LoanDao loanDao;

    public LibraryDatabase() {
        this.authorDao = new FileBasedAuthorDao(AUTHORS_FILE);
        this.categoryDao = new FileBasedCategoryDao(CATEGORIES_FILE);
        this.userDao = new FileBasedUserDao(USERS_FILE);
        this.bookDao = new FileBasedBookDao(BOOKS_FILE, authorDao, categoryDao);
        this.loanDao = new FileBasedLoanDao(LOANS_FILE, bookDao, userDao);
    }

    public List<Author> getAuthors() {
        return authorDao.getAuthors();
    }

    public List<Category> getCategories() {
        return categoryDao.getCategories();
    }

    public List<Book> getBooks() {
        return bookDao.getBooks();
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public List<Loan> getLoans() {
        return loanDao.getLoans();
    }

    public void addAuthor(Author author) {
        authorDao.addAuthor(author);
    }

    public void addCategory(Category category) {
        categoryDao.addCategory(category);
    }

    public void addBook(Book book) {
        bookDao.addBook(book);
    }

    public void addUser(User user) {
        userDao.addUser(user);
    }

    public void addLoan(Loan loan) {
        loanDao.addLoan(loan);
    }

    public void updateLoanReturn(Loan loan) {
        loanDao.updateLoanReturn(loan);
    }

    public void saveAll() {
        authorDao.saveAuthors();
        categoryDao.saveCategories();
        bookDao.saveBooks();
        userDao.saveUsers();
        loanDao.saveLoans();
    }

    public Long getNextAuthorId() {
        return authorDao.getNextAuthorId();
    }

    public Long getNextCategoryId() {
        return categoryDao.getNextCategoryId();
    }

    public Long getNextBookId() {
        return bookDao.getNextBookId();
    }

    public Long getNextUserId() {
        return userDao.getNextUserId();
    }

    public Long getNextLoanId() {
        return loanDao.getNextLoanId();
    }

    public Optional<Author> findAuthorById(Long id) {
        return authorDao.findAuthorById(id);
    }

    public Optional<Category> findCategoryById(Long id) {
        return categoryDao.findCategoryById(id);
    }

    public Optional<Book> findBookById(Long id) {
        return bookDao.findBookById(id);
    }

    public Optional<User> findUserById(Long id) {
        return userDao.findUserById(id);
    }
}