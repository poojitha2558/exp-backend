package com.example.expbackend.repository;

import com.example.expbackend.entity.Expense;
import com.example.expbackend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find all transactions for a specific user
     */
    List<Expense> findByUser(User user);

    /**
     * Find transaction by id and user (safety check - belongs to user)
     */
    Optional<Expense> findByIdAndUser(Long id, User user);

    /**
     * Find transactions by user with pagination
     */
    Page<Expense> findByUser(User user, Pageable pageable);

    /**
     * Find transactions by user and type (expense or income)
     */
    List<Expense> findByUserAndType(User user, String type);

    /**
     * Find transactions by user and category
     */
    List<Expense> findByUserAndCategory(User user, String category);

    /**
     * Find transactions by user, type, and category
     */
    List<Expense> findByUserAndTypeAndCategory(User user, String type, String category);

    /**
     * Find transactions in a specific month by year and month pattern
     * Using LIKE for date matching (date format: YYYY-MM-DD)
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.date LIKE :monthYear AND e.type = :type")
    List<Expense> findByUserAndMonthAndType(@Param("user") User user, @Param("monthYear") String monthYear, @Param("type") String type);

    /**
     * Find all transactions in a specific month
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.date LIKE :monthYear")
    List<Expense> findByUserAndMonth(@Param("user") User user, @Param("monthYear") String monthYear);

    /**
     * Sum of expenses by user and type
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.type = :type")
    Double sumByUserAndType(@Param("user") User user, @Param("type") String type);

    /**
     * Sum of expenses by user, type, and month
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.type = :type AND e.date LIKE :monthYear")
    Double sumByUserAndTypeAndMonth(@Param("user") User user, @Param("type") String type, @Param("monthYear") String monthYear);

    /**
     * Sum of expenses by user and category
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.category = :category")
    Double sumByUserAndCategory(@Param("user") User user, @Param("category") String category);

    /**
     * Sum of expenses by user, category, and type
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.category = :category AND e.type = :type")
    Double sumByUserAndCategoryAndType(@Param("user") User user, @Param("category") String category, @Param("type") String type);

    /**
     * Sum of expenses by user, category, type, and month
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.category = :category AND e.type = :type AND e.date LIKE :monthYear")
    Double sumByUserAndCategoryAndTypeAndMonth(@Param("user") User user, @Param("category") String category, @Param("type") String type, @Param("monthYear") String monthYear);

    /**
     * Get recent transactions (ordered by date, descending)
     */
    @Query("SELECT e FROM Expense e WHERE e.user = :user ORDER BY e.date DESC, e.createdAt DESC")
    List<Expense> findRecentByUser(@Param("user") User user, Pageable pageable);
}

