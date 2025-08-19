package com.expensetracker.expense_service.exception;

public class BudgetExceededException extends RuntimeException{
    public BudgetExceededException(String message) {
        super(message);
    }
}
