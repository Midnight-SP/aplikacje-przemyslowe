package com.techcorp.controller;

import com.techcorp.model.Department;
import com.techcorp.model.Employee;
import com.techcorp.model.Position;
import com.techcorp.service.DepartmentService;
import com.techcorp.service.EmployeeService;
import com.techcorp.service.FileStorageService;
import com.techcorp.model.DocumentType;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/departments")
public class DepartmentViewController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;
    private final FileStorageService storageService;

    public DepartmentViewController(DepartmentService departmentService, EmployeeService employeeService, FileStorageService storageService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
        this.storageService = storageService;
    }

    @GetMapping({"", "/"})
    public String list(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments/list";
    }

    // Alias for direct /departments/list requests
    @GetMapping("/list")
    public String listAlias(Model model) {
        return list(model);
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("managers", getAvailableManagers());
        model.addAttribute("action", "/departments/add");
        return "departments/form";
    }

    @PostMapping("/add")
    public String addSubmit(@Valid @ModelAttribute Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", getAvailableManagers());
            model.addAttribute("action", "/departments/add");
            return "departments/form";
        }
        departmentService.addDepartment(department);
        redirectAttributes.addFlashAttribute("message", "Departament dodany pomyślnie");
        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Department d = departmentService.getById(id);
        model.addAttribute("department", d);
        model.addAttribute("managers", getAvailableManagers());
        model.addAttribute("action", "/departments/edit");
        return "departments/form";
    }

    @PostMapping("/edit")
    public String editSubmit(@Valid @ModelAttribute Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", getAvailableManagers());
            model.addAttribute("action", "/departments/edit");
            return "departments/form";
        }
        departmentService.updateDepartment(department.getId(), department);
        redirectAttributes.addFlashAttribute("message", "Departament zaktualizowany");
        return "redirect:/departments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        departmentService.deleteDepartment(id);
        redirectAttributes.addFlashAttribute("message", "Departament usunięty");
        return "redirect:/departments";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        Department d = departmentService.getById(id);
        model.addAttribute("department", d);
        List<Employee> members = employeeService.getAllEmployees().stream()
                .filter(e -> e.getDepartmentId() != null && e.getDepartmentId().equals(d.getId()))
                .collect(Collectors.toList());
        model.addAttribute("members", members);
        model.addAttribute("managerName", employeeService.findByEmail(d.getManagerEmail()).map(Employee::getFullName).orElse("-"));
        return "departments/details";
    }

    @GetMapping("/documents/{id}")
    public String documents(@PathVariable Long id, Model model) {
        Department d = departmentService.getById(id);
        model.addAttribute("department", d);
        String key = "department-" + id;
        model.addAttribute("documents", storageService.listDocuments(key));
        model.addAttribute("action", "/departments/documents/" + id + "/upload");
        return "departments/documents";
    }

    @PostMapping("/documents/{id}/upload")
    public String uploadDocument(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(name = "type", required = false, defaultValue = "OTHER") DocumentType type,
                                 RedirectAttributes redirectAttributes) {
        Department d = departmentService.getById(id);
        String subPath = "documents/departments/" + id;
        String stored = storageService.storeFile(file, subPath);
        String key = "department-" + id;
        storageService.registerDocument(key, stored, file.getOriginalFilename(), type);
        redirectAttributes.addFlashAttribute("message", "Plik dodany do departamentu");
        return "redirect:/departments/documents/" + id;
    }

    private List<Employee> getAvailableManagers() {
        int managerLevel = Position.MANAGER.getLevel();
        return employeeService.getAllEmployees().stream()
                .filter(e -> e.getPosition() != null && e.getPosition().getLevel() <= managerLevel)
                .collect(Collectors.toList());
    }
}
