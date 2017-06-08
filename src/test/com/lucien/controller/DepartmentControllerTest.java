package com.lucien.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucien.Application;
import com.lucien.exception.NotFoundException;
import com.lucien.model.Department;
import com.lucien.service.IDepartmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by Lucien on 2017/6/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class DepartmentControllerTest {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    private IDepartmentService departmentService;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static final String BASE_URL = "http://localhost:8080/Assignment/api/";

    @Before
    @Transactional
    public void setUp() {

        Department department = new Department();
        department.setName("Department");
        department.setCreatedBy("Lucien");
        department.setModifiedBy("Lucien");
        department.setCreationDate(new Date());
        department.setModificationDate(new Date());
        department.setStatus("A");
        if (!departmentService.isDeptExist(department)) {
            departmentService.addDept(department);
        }

        Department department2 = new Department();
        department2.setName("Department2");
        department2.setCreatedBy("Lucien2");
        department2.setModifiedBy("Lucien2");
        department2.setCreationDate(new Date());
        department2.setModificationDate(new Date());
        department2.setStatus("D");
        if (!departmentService.isDeptExist(department2)) {
            departmentService.addDept(department2);
        }
    }


    @Test
    public void testCreateDeptApiSuccess() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Lucien");
        requestBody.put("status", "A");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.POST, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.CREATED, httpStatus);

            Department department = departmentService.findByName("Lucien");
            try {
                departmentService.deleteDept(department);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateDeptApiDuplicate() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Department");
        requestBody.put("status", "A");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.POST, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.CONFLICT, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateDeptApiFieldError() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", "S");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.POST, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.BAD_REQUEST, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateDeptApiSuccess() {
        Department department = departmentService.findByName("Department");
        Long id = department.getId();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", id);
        requestBody.put("name", "Department");
        requestBody.put("status", "D");
        requestBody.put("createdBy", "Test2");
        requestBody.put("creationDate", getDateWithRightFormat(department.getCreationDate()));
        requestBody.put("modifiedBy", "Test2");
        requestBody.put("modificationDate", getDateWithRightFormat(new Date(System.currentTimeMillis())));
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.PUT, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.OK, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateDeptApiNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", -1);
        requestBody.put("name", "Department");
        requestBody.put("status", "D");
        requestBody.put("createdBy", "Test2");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.PUT, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.NOT_FOUND, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateDeptApiFieldError() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", "S");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.PUT, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.BAD_REQUEST, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteDeptApiSuccess() {
        Department department = departmentService.findByName("Department");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", department.getId());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.DELETE, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.OK, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteDeptApiNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", -1);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.DELETE, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.NOT_FOUND, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDeptApiSuccess() {
        Department department = departmentService.findByName("Department");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", department.getId());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/" + department.getId(), HttpMethod.GET, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.OK, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDeptApiNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", -1);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/" + -1, HttpMethod.GET, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.NOT_FOUND, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllDeptApi() {
        Map<String, Object> requestBody = new HashMap<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.GET, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.OK, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllDeptApiNull() {
        departmentService.deleteAllDept();
        Map<String, Object> requestBody = new HashMap<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "dept/", HttpMethod.GET, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.NOT_FOUND, httpStatus);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getDateWithRightFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
