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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankc.training.employee.entities.Department;
import com.frankc.training.employee.exceptions.EntityNotFoundException;
import com.frankc.training.employee.exceptions.DuplicateDepartmentException;
import com.frankc.training.employee.services.DepartmentService;
/**
 * Unit Tests for DepartmentController.
 *
 * @author Frank Callaly
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DepartmentController.class})
@WebMvcTest
@ActiveProfiles("nomongo")
public class DepartmentControllerTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService mockDepartmentService;

    private static final String TEST_DEPARTMENT_ID = "1A2B3C4D";
    private static final String TEST_DEPARTMENT_NAME = "The Testers";

    @Test
    public void findAllDepartments_returnsList() throws Exception {
        when(mockDepartmentService.findAllDepartments())
            .thenReturn(new ArrayList<Department>());

        MvcResult result = this.mockMvc
                .perform(get(DepartmentController.BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").isNumber())
                .andReturn();

        logger.info("Result from findAllDepartments: "
                    + result.getResponse().getContentAsString());
    }

    @Test
    public void findDepartment_returnsOk() throws Exception {
        Department testDepartment = new Department();
        testDepartment.setId(TEST_DEPARTMENT_ID);
        testDepartment.setName(TEST_DEPARTMENT_NAME);

        when(mockDepartmentService
                .findDepartment(testDepartment.getId()))
            .thenReturn(testDepartment);

        this.mockMvc.perform(get(DepartmentController.BASE_PATH
                                 + testDepartment.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id")
                               .value(testDepartment.getId()))
                    .andExpect(jsonPath("$.name")
                               .value(testDepartment.getName()));
    }

    @Test
    public void findDepartment_unknownReturnsNotFound() throws Exception {
        when(mockDepartmentService.findDepartment(TEST_DEPARTMENT_ID))
            .thenThrow(new EntityNotFoundException("Invalid department id:"
                                                   + TEST_DEPARTMENT_ID));

        this.mockMvc.perform(get(DepartmentController.BASE_PATH
                                 + TEST_DEPARTMENT_ID))
                    .andExpect(status().isNotFound());
    }

    @Test
    public void createDepartment_returnsCreated() throws Exception {
        Department testDepartment = new Department();
        testDepartment.setId(TEST_DEPARTMENT_ID);
        testDepartment.setName(TEST_DEPARTMENT_NAME);

        when(mockDepartmentService.saveDepartment(any()))
            .thenReturn(testDepartment);

        this.mockMvc.perform(
                post(DepartmentController.BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testDepartment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                    .value(testDepartment.getName()));
    }

    @Test
    public void createDepartment_withDuplicateKey_returnsBadRequest() throws Exception {
        Department testDepartment = new Department();
        testDepartment.setId(TEST_DEPARTMENT_ID);
        testDepartment.setName(TEST_DEPARTMENT_NAME);

        when(mockDepartmentService.saveDepartment(any()))
            .thenThrow(new DuplicateDepartmentException());

        this.mockMvc.perform(
                post(DepartmentController.BASE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testDepartment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteDepartment_returnsOk() throws Exception {
        this.mockMvc.perform(delete(DepartmentController.BASE_PATH
                                    + TEST_DEPARTMENT_ID))
                    .andExpect(status().isNoContent());
    }

    @Test
    public void deleteDepartment_unknownReturnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Invalid department id: "
                                            + TEST_DEPARTMENT_ID))
             .when(mockDepartmentService)
             .deleteDepartment(TEST_DEPARTMENT_ID);

        this.mockMvc.perform(delete(DepartmentController.BASE_PATH
                                    + TEST_DEPARTMENT_ID))
                    .andExpect(status().isNotFound());
    }
}
