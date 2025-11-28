package com.techcorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:employees-beans.xml")
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }

    // CommandLineRunner disabled - application now uses database persistence
    // Data loading is handled through web UI or API endpoints
    /*
    @Bean
    @Profile("!test")
    public CommandLineRunner dataLoader(
            EmployeeService employeeService,
            ImportService importService,
            ApiService apiService,
            @Qualifier("xmlEmployees") List<Employee> xmlEmployees,
            @Value("${app.import.csv-file}") String csvPath
    ) {
        return args -> {
            // Console output disabled for Task 8 - migrating to database persistence
        };
    }
    */
}