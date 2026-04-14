package com.example.expbackend.repository;

import com.example.expbackend.entity.Budget;
import com.example.expbackend.entity.Category;
import com.example.expbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Find all budgets for a specific user
     */
    List<Budget> findByUser(User user);

    /**
     * Find budget by user, category, month, and year
     */
    Optional<Budget> findByUserAndCategoryAndMonthAndYear(User user, String category, Integer month, Integer year);

    /**
     * Find all budgets for a specific user and month/year
     */
    List<Budget> findByUserAndMonthAndYear(User user, Integer month, Integer year);

    /**
     * Find all budgets for a specific user and category
     */
    List<Budget> findByUserAndCategory(User user, String category);

    /**
     * Check if budget exists for user, category, month, year
     */
    boolean existsByUserAndCategoryAndMonthAndYear(User user, String category, Integer month, Integer year);


    /**
     * Find budget by id and user (safety check - belongs to user)
     * @param id budget id
     * @param user the user
     * @return Optional containing budget if found and belongs to user
     */
    Optional<Budget> findByIdAndUser(Long id, User user);

    /**
     * Find budget by user, category, and period combination
     * @param user the user
     * @param category the category
     * @param period the budget period (MONTHLY, QUARTERLY, YEARLY)
     * @return Optional containing budget if found
     */
    Optional<Budget> findByUserAndCategoryAndPeriod(User user, Category category, String period);

    /**
     * Check if budget exists for a specific user
     * @param id budget id
     * @param user the user
     * @return true if budget exists and belongs to user
     */
    boolean existsByIdAndUser(Long id, User user);

    /**
     * Find all budgets for a user with a specific period
     * @param user the user
     * @param period the budget period
     * @return list of budgets with the specified period
     */
    List<Budget> findByUserAndPeriod(User user, String period);

    /**
     * Custom query to get budgets by user with spent amount exceeding threshold
     * @param user the user
     * @return list of budgets where spent_amount > amount (over budget)
     */
    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.spentAmount > b.amount")
    List<Budget> findOverBudgetByUser(@Param("user") User user);

    /**
     * Delete budget by id and user (safety check)
     * @param id budget id
     * @param user the user
     */
    void deleteByIdAndUser(Long id, User user);
}

