package com.techcorp.dao;

import com.techcorp.model.Employee;
import com.techcorp.model.EmploymentStatus;
import com.techcorp.model.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JdbcEmployeeDAO using @JdbcTest.
 * Tests verify CRUD operations and enum mapping with an in-memory H2 database.
 */
@JdbcTest
@Import(JdbcEmployeeDAO.class)
@Sql(scripts = "/schema.sql")
class JdbcEmployeeDAOTest {

    @Autowired
    private JdbcEmployeeDAO employeeDAO;

    @Test
    void testSaveAndFindByEmail() {
        // Given
        Employee employee = new Employee();
        employee.setFullName("Jan Kowalski");
        employee.setEmail("jan.kowalski@test.com");
        employee.setCompanyName("TestCorp");
        employee.setPosition(Position.PROGRAMISTA);
        employee.setSalary(10000.0);
        employee.setStatus(EmploymentStatus.ACTIVE);

        // When
        employeeDAO.save(employee);

        // Then
        assertNotNull(employee.getId(), "ID should be generated after insert");
        
        Optional<Employee> found = employeeDAO.findByEmail("jan.kowalski@test.com");
        assertTrue(found.isPresent(), "Employee should be found by email");
        assertEquals("Jan Kowalski", found.get().getFullName());
        assertEquals(Position.PROGRAMISTA, found.get().getPosition());
        assertEquals(EmploymentStatus.ACTIVE, found.get().getStatus());
    }

    @Test
    void testFindByEmailCaseInsensitive() {
        // Given
        Employee employee = new Employee();
        employee.setFullName("Anna Nowak");
        employee.setEmail("Anna.Nowak@Test.COM");
        employee.setCompanyName("TestCorp");
        employee.setPosition(Position.MANAGER);
        employee.setSalary(15000.0);
        employee.setStatus(EmploymentStatus.ACTIVE);
        employeeDAO.save(employee);

        // When - search with different case
        Optional<Employee> found = employeeDAO.findByEmail("anna.nowak@test.com");

        // Then
        assertTrue(found.isPresent(), "Email search should be case-insensitive");
        assertEquals("Anna Nowak", found.get().getFullName());
    }

    @Test
    void testUpdate() {
        // Given - insert employee
        Employee employee = new Employee();
        employee.setFullName("Piotr Wisniewski");
        employee.setEmail("piotr.w@test.com");
        employee.setCompanyName("OldCorp");
        employee.setPosition(Position.PROGRAMISTA);
        employee.setSalary(8000.0);
        employee.setStatus(EmploymentStatus.ACTIVE);
        employeeDAO.save(employee);
        
        Long savedId = employee.getId();

        // When - update employee
        employee.setCompanyName("NewCorp");
        employee.setSalary(12000.0);
        employee.setPosition(Position.MANAGER);
        employeeDAO.save(employee);

        // Then - verify update
        Optional<Employee> updated = employeeDAO.findByEmail("piotr.w@test.com");
        assertTrue(updated.isPresent());
        assertEquals(savedId, updated.get().getId(), "ID should remain the same");
        assertEquals("NewCorp", updated.get().getCompanyName());
        assertEquals(12000.0, updated.get().getSalary());
        assertEquals(Position.MANAGER, updated.get().getPosition());
    }

    @Test
    void testDelete() {
        // Given
        Employee employee = new Employee();
        employee.setFullName("Maria Kowalczyk");
        employee.setEmail("maria.k@test.com");
        employee.setCompanyName("TestCorp");
        employee.setPosition(Position.STAZYSTA);
        employee.setSalary(9000.0);
        employee.setStatus(EmploymentStatus.ACTIVE);
        employeeDAO.save(employee);

        // When
        employeeDAO.delete("maria.k@test.com");

        // Then
        Optional<Employee> deleted = employeeDAO.findByEmail("maria.k@test.com");
        assertFalse(deleted.isPresent(), "Employee should be deleted");
    }

    @Test
    void testFindAll() {
        // Given - insert multiple employees
        Employee emp1 = createEmployee("Adam Nowak", "adam@test.com", "CompanyA", Position.PROGRAMISTA, 10000);
        Employee emp2 = createEmployee("Ewa Kowalska", "ewa@test.com", "CompanyB", Position.MANAGER, 15000);
        Employee emp3 = createEmployee("Tomasz Zielinski", "tomasz@test.com", "CompanyA", Position.STAZYSTA, 8000);
        
        employeeDAO.save(emp1);
        employeeDAO.save(emp2);
        employeeDAO.save(emp3);

        // When
        List<Employee> allEmployees = employeeDAO.findAll();

        // Then
        assertEquals(3, allEmployees.size(), "Should find all 3 employees");
    }

    @Test
    void testDeleteAll() {
        // Given
        Employee emp1 = createEmployee("Test1", "test1@test.com", "Corp", Position.PROGRAMISTA, 10000);
        Employee emp2 = createEmployee("Test2", "test2@test.com", "Corp", Position.MANAGER, 15000);
        employeeDAO.save(emp1);
        employeeDAO.save(emp2);

        // When
        employeeDAO.deleteAll();

        // Then
        List<Employee> allEmployees = employeeDAO.findAll();
        assertTrue(allEmployees.isEmpty(), "All employees should be deleted");
    }

    @Test
    void testEnumMapping() {
        // Given - test all enum values
        Employee employee = new Employee();
        employee.setFullName("Test Employee");
        employee.setEmail("enum.test@test.com");
        employee.setCompanyName("TestCorp");
        employee.setPosition(Position.WICEPREZES);
        employee.setSalary(20000.0);
        employee.setStatus(EmploymentStatus.ON_LEAVE);

        // When
        employeeDAO.save(employee);

        // Then - verify enums are stored and retrieved correctly
        Optional<Employee> found = employeeDAO.findByEmail("enum.test@test.com");
        assertTrue(found.isPresent());
        assertEquals(Position.WICEPREZES, found.get().getPosition(), "Position enum should be mapped correctly");
        assertEquals(EmploymentStatus.ON_LEAVE, found.get().getStatus(), "Status enum should be mapped correctly");
    }

    @Test
    void testGetCompanyStatistics() {
        // Given - employees from multiple companies
        employeeDAO.save(createEmployee("Emp1", "e1@test.com", "CompanyA", Position.PROGRAMISTA, 10000));
        employeeDAO.save(createEmployee("Emp2", "e2@test.com", "CompanyA", Position.MANAGER, 15000));
        employeeDAO.save(createEmployee("Emp3", "e3@test.com", "CompanyB", Position.MANAGER, 20000));
        employeeDAO.save(createEmployee("Emp4", "e4@test.com", "CompanyB", Position.PROGRAMISTA, 12000));

        // When
        var statistics = employeeDAO.getCompanyStatistics();

        // Then
        assertEquals(2, statistics.size(), "Should have statistics for 2 companies");
        
        // Find CompanyA stats
        var companyAStats = statistics.stream()
            .filter(s -> s.getHighestPaidEmployee().startsWith("Emp"))
            .findFirst();
        
        assertTrue(companyAStats.isPresent(), "Should have statistics");
    }

    // Helper method
    private Employee createEmployee(String name, String email, String company, Position position, double salary) {
        Employee emp = new Employee();
        emp.setFullName(name);
        emp.setEmail(email);
        emp.setCompanyName(company);
        emp.setPosition(position);
        emp.setSalary(salary);
        emp.setStatus(EmploymentStatus.ACTIVE);
        return emp;
    }
}
