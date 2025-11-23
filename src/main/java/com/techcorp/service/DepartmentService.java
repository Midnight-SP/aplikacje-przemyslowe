package com.techcorp.service;

import com.techcorp.exception.DepartmentNotFoundException;
import com.techcorp.model.Department;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DepartmentService {
    private final Map<Long, Department> departments = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1000);

    public Department addDepartment(Department department) {
        Objects.requireNonNull(department, "department");
        long id = idGenerator.getAndIncrement();
        department.setId(id);
        departments.put(id, department);
        return department;
    }

    public List<Department> getAllDepartments() {
        return new ArrayList<>(departments.values());
    }

    public Department getById(Long id) {
        Department d = departments.get(id);
        if (d == null) throw new DepartmentNotFoundException(id);
        return d;
    }

    public void updateDepartment(Long id, Department updated) {
        getById(id);
        updated.setId(id);
        departments.put(id, updated);
    }

    public void deleteDepartment(Long id) {
        getById(id);
        departments.remove(id);
    }

    public List<Department> findByManagerEmail(String email) {
        if (email == null) return Collections.emptyList();
        List<Department> res = new ArrayList<>();
        for (Department d : departments.values()) {
            if (email.equalsIgnoreCase(d.getManagerEmail())) res.add(d);
        }
        return res;
    }
}
