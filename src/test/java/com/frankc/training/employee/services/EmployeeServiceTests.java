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
package com.frankc.training.employee.services;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.frankc.training.employee.entities.Employee;
import com.frankc.training.employee.entities.Task;
import com.frankc.training.employee.exceptions.EntityNotFoundException;
import com.frankc.training.employee.repositories.DepartmentRepository;
import com.frankc.training.employee.repositories.EmployeeRepository;

/**
 * Unit Tests for EmployeeService.
 *
 * @author Frank Callaly
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {EmployeeService.class})
@SpringBootTest
@ActiveProfiles("nomongo")
public class EmployeeServiceTests {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository mockEmployeeRepo;

    @MockBean
    private DepartmentRepository mockDepartmentRepo;

    //@SpyBean
    //private ShortUrlPathGenerator shortUrlPathGenerator;

    private static final String TEST_EMPLOYEE_ID = "abcd453";
    private static final String TEST_EMPLOYEE_FULLNAME = "Bob";

    private static final String TEST_TASK_TITLE = "Testing Task";
    private static final String TEST_TASK_DESCRIPTION = "Keep on testing.";

    private static final String TEST_DEPARTMENT_ID = "1A2B3C4D";

    @Test
    public void findAllEmployees_returnsList() {
        when(mockEmployeeRepo.findAll())
            .thenReturn(new ArrayList<Employee>());

        assertThat("findAll should return List",
                   employeeService.findAllEmployees(),
                   instanceOf(List.class));
    }

    @Test
    public void findEmployee_returnsEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);
        testEmployee.setDepartmentId(TEST_DEPARTMENT_ID);

        Task testTask = new Task();
        testTask.setTitle(TEST_TASK_TITLE);
        testTask.setDescription(TEST_TASK_DESCRIPTION);
        testEmployee.setCurrentTask(testTask);

        when(mockEmployeeRepo
                .findById(TEST_EMPLOYEE_ID))
             .thenReturn(Optional.of(testEmployee));

        Employee foundEmployee = employeeService.findEmployee(
                                            TEST_EMPLOYEE_ID);

        assertThat("findEmployee should return Employee",
                   foundEmployee, instanceOf(Employee.class));
        assertEquals("findEmployee should return correct data",
                     foundEmployee.getFullName(),
                     TEST_EMPLOYEE_FULLNAME);
        assertEquals("findEmployee should return correct data",
                foundEmployee.getDepartmentId(),
                TEST_DEPARTMENT_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void findEmployee_unknownThrowsNotFound() {
        when(mockEmployeeRepo
                .findById(TEST_EMPLOYEE_ID))
             .thenReturn(Optional.ofNullable(null));

        employeeService.findEmployee(TEST_EMPLOYEE_ID);
    }

    @Test
    public void createEmployee_returnsEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);
        testEmployee.setDepartmentId(TEST_DEPARTMENT_ID);

        Task testTask = new Task();
        testTask.setTitle(TEST_TASK_TITLE);
        testTask.setDescription(TEST_TASK_DESCRIPTION);
        testEmployee.setCurrentTask(testTask);

        when(mockDepartmentRepo.existsById(TEST_DEPARTMENT_ID))
            .thenReturn(true);
        when(mockEmployeeRepo.save(isA(Employee.class)))
             .thenReturn(testEmployee);

        Employee savedEmployee = employeeService.saveEmployee(
                                            testEmployee);

        assertThat("saveEmployee should return Employee",
                   savedEmployee, instanceOf(Employee.class));
        assertEquals("findByEmployeePath should return correct data",
                     savedEmployee.getFullName(),
                     TEST_EMPLOYEE_FULLNAME);
        assertEquals("findEmployee should return correct data",
                     savedEmployee.getFullName(),
                     TEST_EMPLOYEE_FULLNAME);
        assertEquals("findEmployee should return correct data",
                     savedEmployee.getDepartmentId(),
                     TEST_DEPARTMENT_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void createEmployee_invalidDepartmentIdThrowsNotFound() {
        Employee testEmployee = new Employee();
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);
        testEmployee.setDepartmentId(TEST_DEPARTMENT_ID);

        when(mockDepartmentRepo.existsById(TEST_DEPARTMENT_ID))
             .thenReturn(false);
        employeeService.saveEmployee(testEmployee);
    }

    @Test(expected = EntityNotFoundException.class)
    public void createEmployee_nullDepartmentdThrowsNotFound() {
        Employee testEmployee = new Employee();
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);

        employeeService.saveEmployee(testEmployee);
    }

    @Test
    public void updateEmployee_returnsEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setId(TEST_EMPLOYEE_ID);
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);
        testEmployee.setDepartmentId(TEST_DEPARTMENT_ID);

        Task testTask = new Task();
        testTask.setTitle(TEST_TASK_TITLE);
        testTask.setDescription(TEST_TASK_DESCRIPTION);
        testEmployee.setCurrentTask(testTask);

        when(mockEmployeeRepo.existsById(TEST_EMPLOYEE_ID))
            .thenReturn(true);
        when(mockDepartmentRepo.existsById(TEST_DEPARTMENT_ID))
            .thenReturn(true);
        when(mockEmployeeRepo.save(isA(Employee.class)))
             .thenReturn(testEmployee);

        Employee savedEmployee = employeeService.updateEmployee(
                                            testEmployee);

        assertThat("saveEmployee should return Employee",
                   savedEmployee, instanceOf(Employee.class));
        assertEquals("findByEmployeePath should return correct data",
                     savedEmployee.getFullName(),
                     TEST_EMPLOYEE_FULLNAME);
        assertEquals("findEmployee should return correct data",
                     savedEmployee.getFullName(),
                     TEST_EMPLOYEE_FULLNAME);
        assertEquals("findEmployee should return correct data",
                     savedEmployee.getDepartmentId(),
                     TEST_DEPARTMENT_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void updateEmployee_invalidEmployeeIdThrowsNotFound() {
        Employee testEmployee = new Employee();
        testEmployee.setId(TEST_EMPLOYEE_ID);
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);
        testEmployee.setDepartmentId(TEST_DEPARTMENT_ID);

        when(mockEmployeeRepo.existsById(TEST_DEPARTMENT_ID))
             .thenReturn(false);
        employeeService.updateEmployee(testEmployee);
    }

    @Test(expected = EntityNotFoundException.class)
    public void updateEmployee_nullEmployeeIdThrowsNotFound() {
        Employee testEmployee = new Employee();
        testEmployee.setFullName(TEST_EMPLOYEE_FULLNAME);

        employeeService.updateEmployee(testEmployee);
    }

    @Test
    public void deleteEmployee_noErrors() {
        employeeService.deleteEmployee(TEST_EMPLOYEE_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deletEmployee_unknownThrowsNotFound() {
        doThrow(new IllegalArgumentException())
        .when(mockEmployeeRepo)
        .deleteById(TEST_EMPLOYEE_ID);

        employeeService.deleteEmployee(TEST_EMPLOYEE_ID);
    }
}
