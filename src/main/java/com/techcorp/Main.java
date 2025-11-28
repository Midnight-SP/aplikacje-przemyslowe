package com.techcorp;

/**
 * Legacy main class - no longer used.
 * Application now runs via Spring Boot: EmployeeManagementApplication.
 * 
 * To run the application:
 *   gradle bootRun
 * 
 * Or run the Spring Boot main class directly:
 *   com.techcorp.EmployeeManagementApplication
 * 
 * Web interface available at: http://localhost:8080
 * H2 Console available at: http://localhost:8080/h2-console
 */
@Deprecated
public class Main {
    
    public static void main(String[] args) {
        System.err.println("═══════════════════════════════════════════════════");
        System.err.println("    This main class is DEPRECATED");
        System.err.println("═══════════════════════════════════════════════════");
        System.err.println();
        System.err.println("Please run the application using Spring Boot:");
        System.err.println("  $ gradle bootRun");
        System.err.println();
        System.err.println("Or run the main class:");
        System.err.println("  com.techcorp.EmployeeManagementApplication");
        System.err.println();
        System.err.println("Web UI: http://localhost:8080");
        System.err.println("H2 Console: http://localhost:8080/h2-console");
        System.err.println("═══════════════════════════════════════════════════");
        System.exit(1);
    }
}
