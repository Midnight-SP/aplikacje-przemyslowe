package com.techcorp.dao;

import com.techcorp.model.CompanyStatistics;
import com.techcorp.model.Employee;
import com.techcorp.model.EmploymentStatus;
import com.techcorp.model.Position;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of EmployeeDAO using Spring JdbcTemplate.
 * Provides low-level SQL operations for employee data persistence.
 */
@Repository
public class JdbcEmployeeDAO implements EmployeeDAO {
    
    private final JdbcTemplate jdbcTemplate;
    
    public JdbcEmployeeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * RowMapper that converts database rows to Employee objects.
     * Handles enum mapping from VARCHAR to Java enums.
     */
    private static final RowMapper<Employee> EMPLOYEE_ROW_MAPPER = (rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setId(rs.getLong("id"));
        employee.setFullName(rs.getString("full_name"));
        employee.setEmail(rs.getString("email"));
        employee.setCompanyName(rs.getString("company_name"));
        
        // Map position enum from database string
        String positionStr = rs.getString("position");
        if (positionStr != null) {
            employee.setPosition(Position.valueOf(positionStr));
        }
        
        employee.setSalary(rs.getDouble("salary"));
        
        // Map status enum from database string
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            employee.setStatus(EmploymentStatus.valueOf(statusStr));
        }
        
        employee.setPhotoFileName(rs.getString("photo_file_name"));
        
        Long deptId = rs.getLong("department_id");
        if (!rs.wasNull()) {
            employee.setDepartmentId(deptId);
        }
        
        return employee;
    };
    
    @Override
    public List<Employee> findAll() {
        String sql = "SELECT id, full_name, email, company_name, position, salary, " +
                     "status, photo_file_name, department_id FROM employees";
        return jdbcTemplate.query(sql, EMPLOYEE_ROW_MAPPER);
    }
    
    @Override
    public Optional<Employee> findByEmail(String email) {
        String sql = "SELECT id, full_name, email, company_name, position, salary, " +
                     "status, photo_file_name, department_id FROM employees " +
                     "WHERE LOWER(email) = LOWER(?)";
        List<Employee> results = jdbcTemplate.query(sql, EMPLOYEE_ROW_MAPPER, email);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    @Override
    public void save(Employee employee) {
        if (employee.getId() == null) {
            // INSERT - new employee
            insert(employee);
        } else {
            // UPDATE - existing employee
            update(employee);
        }
    }
    
    /**
     * Inserts a new employee and sets the generated ID.
     */
    private void insert(Employee employee) {
        String sql = "INSERT INTO employees (full_name, email, company_name, position, " +
                     "salary, status, photo_file_name, department_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getCompanyName());
            ps.setString(4, employee.getPosition() != null ? employee.getPosition().name() : null);
            ps.setDouble(5, employee.getSalary());
            ps.setString(6, employee.getStatus() != null ? employee.getStatus().name() : null);
            ps.setString(7, employee.getPhotoFileName());
            
            if (employee.getDepartmentId() != null) {
                ps.setLong(8, employee.getDepartmentId());
            } else {
                ps.setObject(8, null);
            }
            
            return ps;
        }, keyHolder);
        
        // Set the generated ID back to the employee object
        if (keyHolder.getKey() != null) {
            employee.setId(keyHolder.getKey().longValue());
        }
    }
    
    /**
     * Updates an existing employee in the database.
     */
    private void update(Employee employee) {
        String sql = "UPDATE employees SET full_name = ?, email = ?, company_name = ?, " +
                     "position = ?, salary = ?, status = ?, photo_file_name = ?, " +
                     "department_id = ? WHERE id = ?";
        
        jdbcTemplate.update(sql,
            employee.getFullName(),
            employee.getEmail(),
            employee.getCompanyName(),
            employee.getPosition() != null ? employee.getPosition().name() : null,
            employee.getSalary(),
            employee.getStatus() != null ? employee.getStatus().name() : null,
            employee.getPhotoFileName(),
            employee.getDepartmentId(),
            employee.getId()
        );
    }
    
    @Override
    public void delete(String email) {
        String sql = "DELETE FROM employees WHERE LOWER(email) = LOWER(?)";
        jdbcTemplate.update(sql, email);
    }
    
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM employees";
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<CompanyStatistics> getCompanyStatistics() {
        String sql = """
            SELECT 
                company_name,
                COUNT(*) as employee_count,
                AVG(salary) as avg_salary,
                MAX(salary) as max_salary
            FROM employees
            GROUP BY company_name
            ORDER BY company_name
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String companyName = rs.getString("company_name");
            long employeeCount = rs.getLong("employee_count");
            double avgSalary = rs.getDouble("avg_salary");
            double maxSalary = rs.getDouble("max_salary");
            
            // Find the employee with highest salary in this company
            String highestPaidName = findHighestPaidEmployeeInCompany(companyName, maxSalary);
            
            return new CompanyStatistics(employeeCount, avgSalary, highestPaidName);
        });
    }
    
    /**
     * Finds the full name of the employee with the highest salary in a given company.
     * This is a separate query because getting the name in GROUP BY is complex.
     */
    private String findHighestPaidEmployeeInCompany(String companyName, double maxSalary) {
        String sql = "SELECT full_name FROM employees " +
                     "WHERE company_name = ? AND salary = ? " +
                     "LIMIT 1";
        
        List<String> results = jdbcTemplate.query(sql, 
            (rs, rowNum) -> rs.getString("full_name"),
            companyName, 
            maxSalary
        );
        
        return results.isEmpty() ? "" : results.get(0);
    }
}
