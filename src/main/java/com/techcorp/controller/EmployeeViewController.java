package com.techcorp.controller;

import com.techcorp.model.Employee;
import com.techcorp.model.EmploymentStatus;
import com.techcorp.model.Position;
import com.techcorp.service.EmployeeService;
import com.techcorp.service.FileStorageService;
import com.techcorp.service.ImportService;
import com.techcorp.model.ImportSummary;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {

    private final EmployeeService employeeService;
    private final FileStorageService storageService;
    private final ImportService importService;

    public EmployeeViewController(EmployeeService employeeService, FileStorageService storageService, ImportService importService) {
        this.employeeService = employeeService;
        this.storageService = storageService;
        this.importService = importService;
    }

    @GetMapping({"", "/"})
    public String list(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees/list";
    }

    // Alias endpoint so direct request to /employees/list works (avoids 500 from static resource handler)
    @GetMapping("/list")
    public String listAlias(Model model) {
        return list(model);
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("positions", Position.values());
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("action", "/employees/add");
        return "employees/form";
    }

    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute Employee employee, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("positions", Position.values());
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("action", "/employees/add");
            return "employees/form";
        }
        employeeService.addEmployee(employee);
        redirectAttributes.addFlashAttribute("message", "Pracownik dodany pomyślnie");
        return "redirect:/employees";
    }

    @GetMapping("/edit/{email}")
    public String editForm(@PathVariable String email, Model model) {
        Employee existing = employeeService.getByEmail(email);
        model.addAttribute("employee", existing);
        model.addAttribute("positions", Position.values());
        model.addAttribute("statuses", EmploymentStatus.values());
        model.addAttribute("action", "/employees/edit");
        return "employees/form";
    }

    @PostMapping("/edit")
    public String editSubmit(@Valid @ModelAttribute Employee employee, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("positions", Position.values());
            model.addAttribute("statuses", EmploymentStatus.values());
            model.addAttribute("action", "/employees/edit");
            return "employees/form";
        }
        // attempt to update by email key; if email changed, service will handle duplicate checks
        employeeService.updateEmployee(employee.getEmail(), employee);
        redirectAttributes.addFlashAttribute("message", "Dane pracownika zaktualizowane");
        return "redirect:/employees";
    }

    @GetMapping("/delete/{email}")
    public String delete(@PathVariable String email, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(email);
        redirectAttributes.addFlashAttribute("message", "Pracownik usunięty");
        return "redirect:/employees";
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("query", "");
        return "employees/search";
    }

    @PostMapping("/search")
    public String searchResults(@RequestParam("company") String company, Model model) {
        model.addAttribute("employees", employeeService.findByCompany(company));
        model.addAttribute("query", company);
        return "employees/list";
    }

    @GetMapping("/import")
    public String importForm(Model model) {
        model.addAttribute("action", "/employees/import");
        return "employees/import";
    }

    @PostMapping("/import")
    public String importSubmit(@RequestParam("file") MultipartFile file,
                               @RequestParam("fileType") String fileType,
                               RedirectAttributes redirectAttributes) {
        String stored = storageService.storeFile(file, "imports");
        ImportSummary summary;
        if ("xml".equalsIgnoreCase(fileType)) {
            summary = importService.importFromXml(stored);
        } else {
            summary = importService.importFromCsv(stored);
        }
        redirectAttributes.addFlashAttribute("message", "Zaimportowano: " + summary.getImportedCount() + ", błędy: " + summary.getErrors().size());
        if (!summary.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("importErrors", summary.getErrors());
        }
        return "redirect:/employees";
    }
}
