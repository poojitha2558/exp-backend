# Smart Expense Tracker - Complete Architecture Guide

## 📋 Project Overview

A production-ready Smart Expense Tracker backend built with Spring Boot and PostgreSQL that helps users:
- Track daily expenses
- Organize expenses by categories
- Set and monitor budgets
- Get financial analytics and insights
- Receive spending recommendations

---

## 🏗️ Architecture Layers

### **Layer 1: Entity Layer** (Database Models)
**Location:** `src/main/java/com/example/expbackend/entity/`

These are your database tables converted to Java classes.

```
User.java
├─ id (Primary Key)
├─ email (Unique)
├─ password
├─ fullName
├─ monthlyBudget
├─ createdAt, updatedAt

Category.java
├─ id
├─ name (Unique per user)
├─ description
├─ user (Foreign Key → User)
├─ createdAt, updatedAt

Budget.java
├─ id
├─ user (Foreign Key → User)
├─ category (Foreign Key → Category)
├─ amount
├─ period (MONTHLY, QUARTERLY, YEARLY)
├─ spentAmount
├─ createdAt, updatedAt

Expense.java
├─ id
├─ user (Foreign Key → User)
├─ category
├─ amount
├─ description
├─ expenseDate
├─ createdAt, updatedAt
```

**Real-World Analogy:** These are the blueprints of your filing cabinet. Each class defines the structure of data you'll store.

---

### **Layer 2: Repository Layer** (Database Access)
**Location:** `src/main/java/com/example/expbackend/repository/`

Repositories provide methods to interact with the database without writing SQL.

```
UserRepository.java
├─ findByEmail(String email)
├─ existsByEmail(String email)

CategoryRepository.java
├─ findByUser(User user)
├─ findByNameAndUser(String name, User user)
├─ existsByIdAndUser(Long id, User user)
├─ deleteByIdAndUser(Long id, User user)

BudgetRepository.java
├─ findByUser(User user)
├─ findByUserAndCategory(User user, Category category)
├─ findByIdAndUser(Long id, User user)
├─ findByUserAndCategoryAndPeriod(...)
├─ findOverBudgetByUser(User user)

ExpenseRepository.java
├─ findByUser(User user)
├─ findByIdAndUser(Long id, User user)
├─ findByUserAndCategory(User user, String category)
├─ findExpensesByDateRange(User user, Long startDate, Long endDate)
├─ findByUserOrderByExpenseDateDesc(User user)
├─ getTotalSpentByUser(User user)
├─ getTotalSpentByUserAndCategory(User user, String category)
├─ getTotalSpentByUserAndDateRange(User user, Long start, Long end)
```

**Real-World Analogy:** These are your librarians. You ask them to find books, and they search the library for you.

**Why Spring Data JPA?**
- You define method names, Spring generates SQL automatically
- Write less code, fewer bugs
- Built-in security (parameterized queries prevent SQL injection)

---

### **Layer 3: Service Layer** (Business Logic)
**Location:** `src/main/java/com/example/expbackend/service/`

Services contain the core business logic - validation, calculations, and workflows.

```
CategoryService.java
├─ createCategory(User, String name, String description)
├─ getAllCategoriesByUser(User)
├─ getCategoryById(Long id, User)
├─ getCategoryByName(String name, User)
├─ updateCategory(Long id, User, String name, String description)
├─ deleteCategory(Long id, User)
├─ isCategoryNameAvailable(User, String name)
├─ categoryExistsForUser(Long id, User)

BudgetService.java
├─ createBudget(User, Long categoryId, Double amount, String period)
├─ getAllBudgetsByUser(User)
├─ getBudgetById(Long id, User)
├─ getBudgetsByPeriod(User, String period)
├─ getBudgetsByCategory(User, Long categoryId)
├─ updateBudget(Long id, User, Double amount, String period)
├─ resetBudgetSpent(Long id, User)
├─ deleteBudget(Long id, User)
├─ budgetExistsForCategoryAndPeriod(User, Long categoryId, String period)

ExpenseService.java
├─ createExpense(User, String category, Double amount, String description, Long date)
├─ getAllExpensesByUser(User)
├─ getExpenseById(Long id, User)
├─ getExpensesByCategory(User, String category)
├─ getExpensesByDateRange(User, Long start, Long end)
├─ getTotalSpending(User)
├─ getTotalSpendingByCategory(User, String category)
├─ getTotalSpendingByDateRange(User, Long start, Long end)
├─ updateExpense(Long id, User, String category, Double amount, ...)
├─ deleteExpense(Long id, User)
├─ updateBudgetSpentAmounts(User, String category, Double amount)
├─ expenseExistsForUser(Long id, User)

FinancialService.java (NEW - Analytics & Insights)
├─ SPENDING METRICS
│  ├─ getTotalSpending(User)
│  ├─ getTotalSpendingByDateRange(User, Long start, Long end)
│  ├─ getMonthlySpending(User, int year, int month)
│  ├─ getSpendingByCategory(User)
│  ├─ getSpendingByCategoryInRange(User, Long start, Long end)
│  ├─ getAverageExpense(User)
│  ├─ getHighestExpense(User)
│  ├─ getLowestExpense(User)
│  ├─ getTotalExpenseCount(User)
│
├─ BUDGET ANALYSIS
│  ├─ getBudgetStatus(User)
│  ├─ getUtilizationPercentage(Budget)
│  ├─ getWarningLevel(Budget)
│  ├─ getOverBudgetItems(User)
│  ├─ hasOverBudgetItems(User)
│  ├─ getTotalBudgetedAmount(User)
│  ├─ getTotalBudgetSpent(User)
│  ├─ getTotalRemainingBudget(User)
│
├─ FINANCIAL INSIGHTS
│  ├─ getFinancialSummary(User)
│  ├─ getMonthlyFinancialReport(User, int year, int month)
│  ├─ getFinancialHealthScore(User)
│  ├─ getSpendingTrends(User)
│  ├─ getBudgetRecommendations(User)
```

**Real-World Analogy:** These are your accountants/financial advisors. They:
- Validate data before saving (garbage in, garbage out)
- Calculate important metrics
- Ensure business rules are followed
- Make recommendations

**Key Principle:** Controllers shouldn't have business logic. They only:
1. Receive requests
2. Call services
3. Return responses

---

### **Layer 4: Controller Layer** (HTTP Endpoints/REST API)
**Location:** `src/main/java/com/example/expbackend/controller/`

Controllers handle HTTP requests and responses. They're the "interface" between frontend and backend.

```
CategoryController.java
├─ POST   /api/categories                    → createCategory()
├─ GET    /api/categories                    → getAllCategories()
├─ GET    /api/categories/{id}               → getCategoryById()
├─ GET    /api/categories/by-name?name=...   → getCategoryByName()
├─ PUT    /api/categories/{id}               → updateCategory()
├─ DELETE /api/categories/{id}               → deleteCategory()
├─ GET    /api/categories/check-name         → checkNameAvailability()
├─ GET    /api/categories/{id}/exists        → categoryExists()

BudgetController.java
├─ POST   /api/budgets                       → createBudget()
├─ GET    /api/budgets                       → getAllBudgets()
├─ GET    /api/budgets/{id}                  → getBudgetById()
├─ GET    /api/budgets/by-period?period=...  → getBudgetsByPeriod()
├─ GET    /api/budgets/by-category/{id}      → getBudgetsByCategory()
├─ PUT    /api/budgets/{id}                  → updateBudget()
├─ PATCH  /api/budgets/{id}/reset-spent      → resetBudgetSpent()
├─ DELETE /api/budgets/{id}                  → deleteBudget()
├─ GET    /api/budgets/exists                → budgetExists()

ExpenseController.java
├─ POST   /api/expenses                      → createExpense()
├─ GET    /api/expenses                      → getAllExpenses()
├─ GET    /api/expenses/{id}                 → getExpenseById()
├─ GET    /api/expenses/by-category          → getExpensesByCategory()
├─ GET    /api/expenses/by-date-range        → getExpensesByDateRange()
├─ GET    /api/expenses/total-spending       → getTotalSpending()
├─ PUT    /api/expenses/{id}                 → updateExpense()
├─ DELETE /api/expenses/{id}                 → deleteExpense()
├─ GET    /api/expenses/{id}/exists          → expenseExists()

FinancialAnalyticsController.java (NEW - Reports & Insights)
├─ SPENDING OVERVIEW
│  ├─ GET /api/analytics/total-spending
│  ├─ GET /api/analytics/spending-range?startDate=...&endDate=...
│  ├─ GET /api/analytics/monthly-spending?year=...&month=...
│
├─ CATEGORY BREAKDOWN
│  ├─ GET /api/analytics/spending-by-category
│  ├─ GET /api/analytics/spending-by-category-range?startDate=...&endDate=...
│
├─ EXPENSE STATISTICS
│  ├─ GET /api/analytics/average-expense
│  ├─ GET /api/analytics/expense-statistics
│
├─ BUDGET ANALYSIS
│  ├─ GET /api/analytics/budget-status
│  ├─ GET /api/analytics/over-budget-items
│
├─ FINANCIAL INSIGHTS & REPORTS
│  ├─ GET /api/analytics/summary
│  ├─ GET /api/analytics/monthly-report?year=...&month=...
│  ├─ GET /api/analytics/health-score
│  ├─ GET /api/analytics/spending-trends
│  ├─ GET /api/analytics/budget-recommendations
```

**Real-World Analogy:** These are the frontend interfaces/APIs. Like a restaurant:
- Controller = Waiter (takes orders from customers)
- Service = Chef (prepares the food)
- Repository = Warehouse (stores ingredients)

---

## 🔄 Request Flow Example

Let's trace a request for creating an expense:

```
1. FRONTEND sends:
   POST /api/expenses
   Body: {
     "category": "Food",
     "amount": 45.50,
     "description": "Lunch",
     "expenseDate": 1680518400000
   }

2. CONTROLLER receives request
   └─ ExpenseController.createExpense()
   └─ Validates input (already done by @RequestParam)
   └─ Calls service layer

3. SERVICE layer processes business logic
   └─ ExpenseService.createExpense()
   └─ Validates data deeply:
      - Is amount > 0?
      - Is category not empty?
      - Is date not in future?
   └─ Creates Expense object
   └─ Calls repository to save

4. REPOSITORY saves to database
   └─ ExpenseRepository.save(expense)
   └─ Uses JPA to generate SQL
   └─ PostgreSQL executes INSERT statement
   └─ Database returns created expense with ID

5. SERVICE updates budgets
   └─ updateBudgetSpentAmounts()
   └─ Finds budgets for this category
   └─ Updates spent_amount field
   └─ Calls repository again

6. CONTROLLER returns response
   └─ Status: 201 CREATED
   └─ Body: { "message": "...", "expense": { ... } }

7. FRONTEND receives response and displays to user
```

---

## 🔒 Security Features Implemented

### 1. **User Isolation (Multi-Tenancy)**
Every query filters by user:
```java
// ❌ WRONG - Could see other users' data
expenses = expenseRepository.findAll();

// ✅ CORRECT - Only this user's data
expenses = expenseRepository.findByUser(user);
```

### 2. **Parameterized Queries (SQL Injection Prevention)**
Spring Data JPA uses parameterized queries automatically:
```java
// ✅ SAFE - Parameter is escaped
repository.findByUser(:user)

// ❌ DANGEROUS - Raw SQL concatenation (never do this!)
// "SELECT * FROM expenses WHERE user_id = " + userId
```

### 3. **Authentication** (Spring Security)
- User must be authenticated to access endpoints
- `@AuthenticationPrincipal User user` ensures only authenticated users can call methods

### 4. **Authorization** (Implicit)
- Users can only access their own data
- Repositories verify user ownership

---

## 📊 Database Schema

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    monthly_budget DOUBLE PRECISION,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

-- Categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at BIGINT NOT NULL,
    updated_at BIGINT,
    UNIQUE(name, user_id)  -- Each user has unique category names
);

-- Budgets table
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount DOUBLE PRECISION NOT NULL,
    period VARCHAR(50) NOT NULL,  -- MONTHLY, QUARTERLY, YEARLY
    spent_amount DOUBLE PRECISION DEFAULT 0.0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT,
    UNIQUE(user_id, category_id, period)  -- One budget per user/category/period
);

-- Expenses table
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    description TEXT,
    expense_date BIGINT NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT,
    INDEX idx_user_date (user_id, expense_date)  -- For date range queries
);
```

---

## 🎯 Data Flow Examples

### Example 1: Create Expense
```
Request → CategoryController.createExpense()
       → ExpenseService.createExpense()
       → Validates (amount > 0, category exists, date <= now)
       → ExpenseRepository.save()
       → Database: INSERT into expenses
       → Update budgets
       → Response ✅
```

### Example 2: Get Financial Summary
```
Request → FinancialAnalyticsController.getFinancialSummary()
       → FinancialService.getFinancialSummary()
         ├─ getTotalSpending() → ExpenseRepository.getTotalSpentByUser()
         ├─ getAverageExpense() → Calculate from expenses
         ├─ getSpendingByCategory() → Stream & group expenses
         ├─ getBudgetStatus() → BudgetRepository.findByUser()
         └─ hasOverBudgetItems() → Filter budgets
       → Response: { totalSpending, budgets, categories, trends, ... }
```

### Example 3: Update Budget
```
Request → BudgetController.updateBudget()
       → BudgetService.updateBudget()
         ├─ Validate (user owns budget, amount >= 0)
         ├─ Update fields
         └─ BudgetRepository.save()
       → Database: UPDATE budgets
       → Response ✅
```

---

## 🚀 API Endpoint Summary

### Categories
```
POST   /api/categories                    - Create category
GET    /api/categories                    - List all categories
GET    /api/categories/{id}               - Get specific category
PUT    /api/categories/{id}               - Update category
DELETE /api/categories/{id}               - Delete category
GET    /api/categories/check-name         - Check name availability
GET    /api/categories/{id}/exists        - Check existence
```

### Budgets
```
POST   /api/budgets                       - Create budget
GET    /api/budgets                       - List all budgets
GET    /api/budgets/{id}                  - Get specific budget
GET    /api/budgets/by-period             - Filter by period
GET    /api/budgets/by-category/{id}      - Filter by category
PUT    /api/budgets/{id}                  - Update budget
PATCH  /api/budgets/{id}/reset-spent      - Reset spent amount
DELETE /api/budgets/{id}                  - Delete budget
GET    /api/budgets/exists                - Check existence
```

### Expenses
```
POST   /api/expenses                      - Create expense
GET    /api/expenses                      - List all expenses
GET    /api/expenses/{id}                 - Get specific expense
GET    /api/expenses/by-category          - Filter by category
GET    /api/expenses/by-date-range        - Filter by date range
GET    /api/expenses/total-spending       - Get total
PUT    /api/expenses/{id}                 - Update expense
DELETE /api/expenses/{id}                 - Delete expense
GET    /api/expenses/{id}/exists          - Check existence
```

### Analytics (Financial Insights)
```
GET    /api/analytics/total-spending      - Total spending
GET    /api/analytics/spending-range      - Spending in date range
GET    /api/analytics/monthly-spending    - Spending by month
GET    /api/analytics/spending-by-category - Category breakdown
GET    /api/analytics/spending-by-category-range - Category breakdown by date
GET    /api/analytics/average-expense     - Average transaction amount
GET    /api/analytics/expense-statistics  - Min, max, average, count
GET    /api/analytics/budget-status       - All budgets status
GET    /api/analytics/over-budget-items   - Exceeded budgets
GET    /api/analytics/summary             - Complete financial dashboard
GET    /api/analytics/monthly-report      - Monthly financial report
GET    /api/analytics/health-score        - Financial health score (0-100)
GET    /api/analytics/spending-trends     - Last 6 months trends
GET    /api/analytics/budget-recommendations - Smart budget suggestions
```

---

## 🛠️ Configuration Files

### `application.properties`
Stores database connection, JWT secret, logging levels, etc.

**Key Settings:**
- `spring.datasource.url` - PostgreSQL connection
- `spring.jpa.hibernate.ddl-auto=update` - Auto-create/update tables
- `app.jwt.secret` - Secret key for JWT tokens
- `spring.jpa.show-sql=false` - Hide SQL logs (use for debugging)

---

## ✅ Validation Strategy

### Input Validation (Controllers)
- `@RequestParam` - Spring validates required parameters
- Response `400 BAD REQUEST` if invalid

### Business Logic Validation (Services)
```java
if (amount <= 0) {
    throw new IllegalArgumentException("Amount must be > 0");
}
```

### Database Constraints (Entities)
```java
@Column(nullable = false, unique = true)
private String email;
```

---

## 📈 Scalability Considerations

### Current Architecture is Ready For:
1. **Multi-user support** - ✅ Every query filters by user
2. **Large datasets** - ✅ JPA handles pagination, queries optimized
3. **Real-time updates** - ✅ RESTful API supports polling
4. **Future enhancements** - ✅ Service layer is decoupled from controllers

### Future Improvements:
1. Add authentication endpoint (login/register)
2. Add pagination to list endpoints
3. Add caching (Redis) for analytics
4. Add recurring expense support
5. Add multi-currency support
6. Add email notifications for budget alerts
7. Add file export (CSV/PDF reports)

---

## 🎓 Learning Path

To understand this codebase:

1. **Start with Entities** → Understand data model
2. **Then Repositories** → Understand database queries
3. **Then Services** → Understand business logic
4. **Finally Controllers** → Understand API endpoints
5. **Then FinancialService** → Understand analytics

Each layer builds on the previous one.

---

## 📝 Code Quality Standards

### Followed:
- ✅ Single Responsibility Principle (each class has one job)
- ✅ Dependency Injection (using `@RequiredArgsConstructor`)
- ✅ Transactional consistency (using `@Transactional`)
- ✅ Javadoc comments (explaining what, why, and how)
- ✅ RESTful principles (using proper HTTP verbs and status codes)
- ✅ Security first (user isolation, parameterized queries)
- ✅ Error handling (try-catch, meaningful error messages)

---

## 🚀 Next Steps for Production

1. **Authentication Service** - Login/Register endpoints with JWT
2. **Email Notifications** - Alert users when over budget
3. **Data Export** - Generate CSV/PDF reports
4. **API Rate Limiting** - Prevent abuse
5. **Logging & Monitoring** - Track system health
6. **Unit Tests** - Test each layer
7. **Integration Tests** - Test full workflows
8. **CI/CD Pipeline** - Automated testing and deployment


