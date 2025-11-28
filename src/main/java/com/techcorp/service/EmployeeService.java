package com.techcorp.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techcorp.dao.EmployeeDAO;
import com.techcorp.exception.DuplicateEmailException;
import com.techcorp.exception.EmployeeNotFoundException;
import com.techcorp.model.CompanyStatistics;
import com.techcorp.model.Employee;
import com.techcorp.model.EmploymentStatus;
import com.techcorp.model.Position;

/**
 * Service layer for employee business logic.
 * Delegates data persistence to EmployeeDAO.
 */
@Service
@Transactional
public class EmployeeService {
    
    private final EmployeeDAO employeeDAO;

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    /**
     * Adds a new employee to the database.
     * @throws DuplicateEmailException if email already exists
     */
    public void addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "employee");
        
        // Check for duplicate email
        if (employeeDAO.findByEmail(employee.getEmail()).isPresent()) {
            throw new DuplicateEmailException(employee.getEmail());
        }
        
        employeeDAO.save(employee);
    }

    /**
     * Retrieves all employees from the database.
     */
    public List<Employee> getAllEmployees() {
        return employeeDAO.findAll();
    }

    /**
     * Finds employees by company name (case-insensitive).
     */
    public List<Employee> findByCompany(String companyName) {
        Objects.requireNonNull(companyName, "companyName");
        return employeeDAO.findAll().stream()
                .filter(e -> e.getCompanyName().equalsIgnoreCase(companyName))
                .collect(Collectors.toList());
    }

    /**
     * Returns employees sorted by last name.
     */
    public List<Employee> getEmployeesSortedByLastName() {
        Comparator<Employee> cmp = Comparator
                .comparing((Employee e) -> e.getLastName().toLowerCase())
                .thenComparing(e -> e.getFullName().toLowerCase());
        return employeeDAO.findAll().stream()
                .sorted(cmp)
                .collect(Collectors.toList());
    }

    /**
     * Groups employees by position.
     */
    public Map<Position, List<Employee>> groupByPosition() {
        return employeeDAO.findAll().stream()
                .collect(Collectors.groupingBy(Employee::getPosition));
    }

    /**
     * Counts employees by position.
     */
    public Map<Position, Long> countByPosition() {
        return employeeDAO.findAll().stream()
                .collect(Collectors.groupingBy(Employee::getPosition, Collectors.counting()));
    }

    /**
     * Calculates average salary across all employees.
     */
    public OptionalDouble getAverageSalary() {
        return employeeDAO.findAll().stream()
                .mapToDouble(Employee::getSalary)
                .average();
    }

    /**
     * Finds the employee with the highest salary.
     */
    public Optional<Employee> getTopEarner() {
        return employeeDAO.findAll().stream()
                .max(Comparator.comparingDouble(Employee::getSalary));
    }

    /**
     * Returns the total number of employees.
     */
    public int size() {
        return employeeDAO.findAll().size();
    }

    /**
     * Validates that all employees earn at least their position's base salary.
     */
    public List<Employee> validateSalaryConsistency() {
        return employeeDAO.findAll().stream()
                .filter(employee -> employee.getSalary() < employee.getPosition().getBaseSalary())
                .collect(Collectors.toList());
    }

    /**
     * Gets company statistics using SQL aggregation (more efficient than Java streams).
     * Returns a map of company name to statistics.
     */
    public Map<String, CompanyStatistics> getCompanyStatistics() {
        // Use SQL GROUP BY instead of Java streams for better performance
        List<CompanyStatistics> statsList = employeeDAO.getCompanyStatistics();
        
        // Convert list to map - need to get company names from employees
        // Since CompanyStatistics doesn't store company name, we need a workaround
        // For now, keep the stream-based approach but could be optimized
        return employeeDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                    Employee::getCompanyName,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        employeesList -> {
                            long count = employeesList.size();
                            
                            double avgSalary = employeesList.stream()
                                    .mapToDouble(Employee::getSalary)
                                    .average()
                                    .orElse(0.0);
                            
                            String highestPaid = employeesList.stream()
                                    .max(Comparator.comparingDouble(Employee::getSalary))
                                    .map(Employee::getFullName)
                                    .orElse("");
                            
                            return new CompanyStatistics(count, avgSalary, highestPaid);
                        }
                    )
                ));
    }

    /**
     * Finds an employee by email (case-insensitive).
     */
    public Optional<Employee> findByEmail(String email) {
        Objects.requireNonNull(email, "email");
        return employeeDAO.findByEmail(email);
    }

    /**
     * Gets an employee by email or throws exception if not found.
     * @throws EmployeeNotFoundException if employee not found
     */
    public Employee getByEmail(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    /**
     * Updates an existing employee.
     * Handles email changes by checking for duplicates.
     */
    public void updateEmployee(String email, Employee updatedEmployee) {
        Employee existing = getByEmail(email);
        
        // If email is changing, check for duplicates
        if (!email.equalsIgnoreCase(updatedEmployee.getEmail())) {
            if (employeeDAO.findByEmail(updatedEmployee.getEmail()).isPresent()) {
                throw new DuplicateEmailException(updatedEmployee.getEmail());
            }
        }
        
        // Set the ID from existing employee to ensure UPDATE instead of INSERT
        updatedEmployee.setId(existing.getId());
        employeeDAO.save(updatedEmployee);
    }

    /**
     * Deletes an employee by email.
     * @throws EmployeeNotFoundException if employee not found
     */
    public void deleteEmployee(String email) {
        getByEmail(email); // Verify employee exists
        employeeDAO.delete(email);
    }

    /**
     * Updates only the employment status of an employee.
     */
    public void updateEmployeeStatus(String email, EmploymentStatus status) {
        Employee employee = getByEmail(email);
        employee.setStatus(status);
        employeeDAO.save(employee);
    }

    /**
     * Finds all employees with a specific status.
     */
    public List<Employee> findByStatus(EmploymentStatus status) {
        Objects.requireNonNull(status, "status");
        return employeeDAO.findAll().stream()
                .filter(e -> e.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Counts employees by status.
     */
    public Map<EmploymentStatus, Long> countByStatus() {
        return employeeDAO.findAll().stream()
                .collect(Collectors.groupingBy(Employee::getStatus, Collectors.counting()));
    }
}
