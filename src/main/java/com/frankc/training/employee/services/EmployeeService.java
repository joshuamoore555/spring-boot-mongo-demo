package com.frankc.training.employee.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.frankc.training.employee.entities.Employee;
import com.frankc.training.employee.exceptions.EntityNotFoundException;
import com.frankc.training.employee.repositories.DepartmentRepository;
import com.frankc.training.employee.repositories.EmployeeRepository;

@Service
public class EmployeeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private DepartmentRepository departmentRepo;

    public List<Employee> findAllEmployees() {
        return employeeRepo.findAll();
    }

    public Employee findEmployee(String employeeId) {
        Optional<Employee> employee = employeeRepo.findById(employeeId);

        if (!employee.isPresent()) {
            throw new EntityNotFoundException(
                                "Invalid employee id: " + employeeId);
        }

        logger.info("Found employee: " + employee.get());
        return employee.get();
    }

    public Employee saveEmployee(Employee employee) {
        if (employee.getDepartmentId() == null ||
            !departmentRepo.existsById(employee.getDepartmentId())) {
            throw new EntityNotFoundException(
                            "Invalid department id: "
                            + employee.getDepartmentId());
        }

        logger.info("Saving employee: " + employee);
        return employeeRepo.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        if (employee.getId() == null ||
            !employeeRepo.existsById(employee.getId())) {
            throw new EntityNotFoundException("Invalid employee id: "
                                              + employee.getId());
        }

        return saveEmployee(employee);
    }

    public void deleteEmployee(String id) {
        try {
            employeeRepo.deleteById(id);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException("Invalid employee id: " + id);
        }
    }
}
