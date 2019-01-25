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
package com.frankc.training.employee.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.frankc.training.employee.controllers.DepartmentController;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
/**
 * Unit Tests for DepartmentController.
 *
 * @author Frank Callaly
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@IfProfileValue(name="spring.profiles.active", value="integration-test")
@ActiveProfiles("init-data")
public class ApplicationIntegrationTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    int serverPort;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void departmentController_findAll() {
        logger.info("********* INTEGRATION TESTS **************");

        String result = this.restTemplate.getForObject(
                "http://localhost:" + serverPort +
                DepartmentController.BASE_PATH, String.class);

        logger.info("Received Departments: " + result);

        DocumentContext jsonData = JsonPath.parse(result);
        assertEquals("Expected two departments in init-data.json",
                   (Integer)jsonData.read("$.size()"), new Integer(2));

        assertEquals("Expected Sales as first department",
                     (String)jsonData.read("$[0].name"), "Sales");
        assertEquals("Expected Marketing as second department",
                (String)jsonData.read("$[1].name"), "Marketing");
    }
}
