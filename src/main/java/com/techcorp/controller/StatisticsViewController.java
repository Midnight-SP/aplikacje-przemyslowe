package com.techcorp.controller;

import com.techcorp.model.CompanyStatistics;
import com.techcorp.service.DepartmentService;
import com.techcorp.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/statistics")
public class StatisticsViewController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public StatisticsViewController(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        int totalEmployees = employeeService.size();
        double avgSalary = employeeService.getAverageSalary().orElse(0.0);
        int totalDepartments = departmentService.getAllDepartments().size();

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("averageSalary", avgSalary);
        model.addAttribute("totalDepartments", totalDepartments);

        Map<String, CompanyStatistics> companyStats = employeeService.getCompanyStatistics();
        model.addAttribute("companyStats", companyStats);

        model.addAttribute("positionDistribution", employeeService.countByPosition());

        return "statistics/index";
    }

    @GetMapping("/company/{name}")
    public String companyDetails(@PathVariable String name, Model model) {
        CompanyStatistics stats = employeeService.getCompanyStatistics().get(name);
        model.addAttribute("companyName", name);
        model.addAttribute("stats", stats);
        model.addAttribute("employees", employeeService.findByCompany(name));
        return "statistics/company";
    }
}
