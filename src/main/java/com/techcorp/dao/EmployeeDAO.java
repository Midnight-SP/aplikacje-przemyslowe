package com.techcorp.dao;

import com.techcorp.model.CompanyStatistics;
import com.techcorp.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Employee entities.
 * Provides CRUD operations and analytics queries for employees stored in the database.
 */
public interface EmployeeDAO {
    
    /**
     * Retrieves all employees from the database.
     * @return list of all employees
     */
    List<Employee> findAll();
    
    /**
     * Finds an employee by their email address.
     * @param email the email address to search for (case-insensitive)
     * @return Optional containing the employee if found, empty otherwise
     */
    Optional<Employee> findByEmail(String email);
    
    /**
     * Saves an employee to the database.
     * If the employee has no ID (null), performs INSERT.
     * If the employee has an ID, performs UPDATE.
     * @param employee the employee to save
     */
    void save(Employee employee);
    
    /**
     * Deletes an employee by their email address.
     * @param email the email address of the employee to delete
     */
    void delete(String email);
    
    /**
     * Deletes all employees from the database.
     * Useful for clearing the database before bulk imports.
     */
    void deleteAll();
    
    /**
     * Computes company statistics using SQL aggregation.
     * Groups employees by company and calculates:
     * - Number of employees (COUNT)
     * - Average salary (AVG)
     * - Highest paid employee name
     * @return list of company statistics
     */
    List<CompanyStatistics> getCompanyStatistics();
}
