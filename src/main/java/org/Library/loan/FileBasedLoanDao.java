package org.Library.loan;

import org.Library.book.Book;
import org.Library.book.BookDao;
import org.Library.user.UserDao;
import org.Library.user.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileBasedLoanDao implements LoanDao {
    private final Path loansFile;
    private final BookDao bookDao;
    private final UserDao userDao;
    private final List<Loan> loans = new ArrayList<>();

    public FileBasedLoanDao(Path loansFile, BookDao bookDao, UserDao userDao) {
        this.loansFile = loansFile;
        this.bookDao = bookDao;
        this.userDao = userDao;

        createFileIfMissing();
        loadLoans();
    }

    @Override
    public List<Loan> getLoans() {
        return loans;
    }

    @Override
    public void addLoan(Loan loan) {
        loans.add(loan);
        saveLoans();
        bookDao.saveBooks();
    }

    @Override
    public void updateLoanReturn(Loan loan) {
        saveLoans();
        bookDao.saveBooks();
    }

    @Override
    public void saveLoans() {
        List<String> lines = loans.stream()
                .map(loan -> loan.getId() + "|" +
                        getBookId(loan) + "|" +
                        getUserId(loan) + "|" +
                        loan.getBorrowedAt() + "|" +
                        loan.getReturnDate() + "|" +
                        loan.isReturned())
                .toList();

        writeLines(lines);
    }

    @Override
    public Long getNextLoanId() {
        return loans.stream()
                .map(Loan::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private void loadLoans() {
        readLines().forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 6) {
                Book book = bookDao.findBookById(Long.parseLong(parts[1])).orElse(null);
                User user = userDao.findUserById(Long.parseLong(parts[2])).orElse(null);

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

    private Long getBookId(Loan loan) {
        return loan.getBook() == null ? 0L : loan.getBook().getId();
    }

    private Long getUserId(Loan loan) {
        return loan.getUser() == null ? 0L : loan.getUser().getId();
    }

    private void createFileIfMissing() {
        try {
            if (loansFile.getParent() != null) {
                Files.createDirectories(loansFile.getParent());
            }

            if (!Files.exists(loansFile)) {
                Files.createFile(loansFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create loans file: " + loansFile, e);
        }
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(loansFile)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read loans file: " + loansFile, e);
        }
    }

    private void writeLines(List<String> lines) {
        try {
            Files.write(
                    loansFile,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write loans file: " + loansFile, e);
        }
    }
}