package org.Library.Loan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.Library.Book.Book;
import org.Library.Common.Displayable;
import org.Library.Common.Identifiable;
import org.Library.User.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
public class Loan implements Identifiable, Displayable {
    private Long id;
    private Book book;
    private User user;
    private LocalDate borrowedAt;
    private LocalDate returnDate;
    private boolean returned;

    public Loan(Long id, Book book, User user, LocalDate borrowedAt, LocalDate returnDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.borrowedAt = borrowedAt;
        this.returnDate = returnDate;
        this.returned = false;

        if (book != null) {
            book.borrowBook();
        }
    }

    public void markReturned() {
        returned = true;

        if (book != null) {
            book.returnBook();
        }
    }

    public boolean isOverdue() {
        return !returned && returnDate != null && LocalDate.now().isAfter(returnDate);
    }

    public long getLoanDurationDays() {
        if (borrowedAt == null || returnDate == null) {
            return 0;
        }

        return ChronoUnit.DAYS.between(borrowedAt, returnDate);
    }

    @Override
    public String getDisplayInfo() {
        String bookTitle = book == null ? "Unknown Book" : book.getTitle();
        String userName = user == null ? "Unknown User" : user.getName();

        return "Loan{id=" + id +
                ", book='" + bookTitle + '\'' +
                ", user='" + userName + '\'' +
                ", borrowedAt=" + borrowedAt +
                ", returnDate=" + returnDate +
                ", returned=" + returned +
                ", overdue=" + isOverdue() +
                '}';
    }
}
