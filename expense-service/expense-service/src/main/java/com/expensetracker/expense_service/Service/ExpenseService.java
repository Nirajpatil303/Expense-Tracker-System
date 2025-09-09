package com.expensetracker.expense_service.Service;

import com.expensetracker.expense_service.Client.UserClient;
import com.expensetracker.expense_service.Entity.Expense;
import com.expensetracker.expense_service.Repository.ExpenseRepository;
import com.expensetracker.expense_service.dto.UserDto;
import com.expensetracker.expense_service.exception.BudgetExceededException;
import com.expensetracker.expense_service.exception.UserNotFoundException;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserClient userClient; //  Inject Feign Client

    public Expense createExpense(Expense expense) {

        //validateUserExists(expense.getUserId()); added New Validation method
        validateUserAndBudget(expense.getUserId(), BigDecimal.valueOf(expense.getAmount()));
        return expenseRepository.save(expense);
    }
    public void validateUserExists(Long userId) {
        try {
            userClient.getUserById(userId); // Feign client call
        } catch (FeignException.NotFound ex) {
            // This is hard codeing httpstatus so dont do it better to do custom exception thing
           // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found in User Service");
            throw new UserNotFoundException("User not found in User Service");
        } catch (FeignException ex) {
            // This is hard codeing httpstatus so dont do it better to do custom exception thing
            //throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service error");

            throw new UserNotFoundException("User Service is temporarily unavailable");
        }
    }

    public void validateUserAndBudget(Long userId, BigDecimal newExpenseAmount) {
        System.out.println("validateUserAndBudget called with userId: " + userId + ", amount: " + newExpenseAmount);

        UserDto user;
        try {
            user = userClient.getUserById(userId);
            System.out.println("User retrieved from User Service: " + user.getName());
        } catch (FeignException.NotFound e) {
            System.out.println("User not found in User Service");
            throw new UserNotFoundException("User not found in User Service");
        } catch (Exception e) {
            System.out.println("Error calling User Service: " + e.getMessage());
            throw e;
        }
    }
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public Expense updateExpense(Long id, Expense updated) {
        UserDto user = userClient.getUserById(updated.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Expense existing = getExpenseById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setAmount(updated.getAmount());
        existing.setDate(updated.getDate());
        existing.setUserId(updated.getUserId());
        return expenseRepository.save(existing);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }
}
