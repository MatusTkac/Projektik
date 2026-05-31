package org.Library.History;

import org.Library.Loan.Loan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class HistoryService {
    private final Path historyFilePath;

    public HistoryService(String filePath) {
        this.historyFilePath = Path.of(filePath);
        createFileIfMissing();
    }

    public void saveLoanCreated(Loan loan) {
        writeLine(formatLoanRecord("LOAN_CREATED", loan));
    }

    public void saveLoanReturned(Loan loan) {
        writeLine(formatLoanRecord("LOAN_RETURNED", loan));
    }

    public void saveMessage(String message) {
        writeLine("[MESSAGE] " + LocalDateTime.now() + " | " + message);
    }

    private String formatLoanRecord(String action, Loan loan) {
        String bookTitle = loan.getBook() == null ? "Unknown Book" : loan.getBook().getTitle();
        String userName = loan.getUser() == null ? "Unknown User" : loan.getUser().getName();
        String userEmail = loan.getUser() == null ? "Unknown Email" : loan.getUser().getEmail();

        return "[" + action + "] " +
                "loggedAt=" + LocalDateTime.now() +
                " | loanId=" + loan.getId() +
                " | bookTitle=\"" + bookTitle + "\"" +
                " | userName=\"" + userName + "\"" +
                " | userEmail=\"" + userEmail + "\"" +
                " | borrowedAt=" + loan.getBorrowedAt() +
                " | returnDate=" + loan.getReturnDate() +
                " | returned=" + loan.isReturned() +
                " | overdue=" + loan.isOverdue();
    }

    private void createFileIfMissing() {
        try {
            if (historyFilePath.getParent() != null) {
                Files.createDirectories(historyFilePath.getParent());
            }

            if (!Files.exists(historyFilePath)) {
                Files.createFile(historyFilePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create history file: " + historyFilePath, e);
        }
    }

    private void writeLine(String line) {
        try {
            Files.writeString(
                    historyFilePath,
                    line + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write to history file: " + historyFilePath, e);
        }
    }
}