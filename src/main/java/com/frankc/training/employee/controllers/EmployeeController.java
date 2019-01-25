/*******************************************************************************
 * Copyright (C) 2019 Frank Callaly
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.frankc.training.employee.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankc.training.employee.entities.Employee;
import com.frankc.training.employee.services.EmployeeService;

/**
 * Implements interface from RESTful HTTP/JSON to Employee Repository.
 *
 * @author Frank Callaly
 */
@RestController
@RequestMapping(EmployeeController.BASE_PATH)
public class EmployeeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String BASE_PATH = "/employees/";

    @Autowired
    private EmployeeService employeeService;

    /**
     * Get a collection of all Employees in the repository.
     *
     * @return HttpEntity containing List of Employees
     */
    @GetMapping
    public HttpEntity<List<Employee>> findAllEmployees() {
        return new ResponseEntity<List<Employee>>(
                                    employeeService.findAllEmployees(),
                                    HttpStatus.OK);
    }

    /**
     * Get a single particular Employee from the repository.
     *
     * @param id of Employee to return, taken from path
     * @return HttpEntity containing Employee corresponding to the given id
     */
    @GetMapping("{id}")
    public HttpEntity<Employee> getEmployee(@PathVariable("id") final String id) {
        logger.info("getEmployee: " + id);

        return new ResponseEntity<Employee>(employeeService.findEmployee(id),
                                            HttpStatus.OK);
    }

    /**
     * Create a new Employee and add to the repository.
     *
     * @param newEmployee Employee corresponding to data sent in request body
     * @return HttpEntity containing Employee that has been added
     */
    @PostMapping
    public HttpEntity<Employee> createEmployee(@RequestBody final Employee newEmployee) {
        logger.info("createEmployee: " + newEmployee);

        return new ResponseEntity<Employee>(employeeService.saveEmployee(newEmployee),
                                            HttpStatus.CREATED);
    }

    
    /**
     * Update an Employee record.
     *
     * @param id of employee to update
     * @return HttpEntity containing updated Employee on success
     */
    @PutMapping("{id}")
    public HttpEntity<Employee> updateEmployee(@PathVariable("id") final String id,
                                               @RequestBody final Employee employee) {
        // Id from the path takes priority
        employee.setId(id);
        logger.info("updateEmployee: " + employee);

        return new ResponseEntity<Employee>(employeeService.updateEmployee(employee),
                                            HttpStatus.OK);
    }

    /**
     * Delete a Employee from the repository.
     *
     * @param id of the Employee to delete
     * @return HttpEntity containing NO_CONTENT
     */
    @DeleteMapping("{id}")
    public HttpEntity<Employee> deleteEmployee(@PathVariable("id") final String id) {
        logger.info("deleteEmployee: " + id);

        employeeService.deleteEmployee(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
