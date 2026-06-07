package org.Library.loan;

import java.util.List;

public interface LoanDao {

    List<Loan> getLoans();

    void addLoan(Loan loan);

    void updateLoanReturn(Loan loan);

    void saveLoans();

    Long getNextLoanId();
}