package com.lucien.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucien.Application;
import com.lucien.exception.NotFoundException;
import com.lucien.model.Department;
import com.lucien.model.User;
import com.lucien.service.IDepartmentService;
import com.lucien.service.IUserService;
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
public class UserControllerTest {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IDepartmentService departmentService;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static final String BASE_URL = "http://localhost:8080/Assignment/api/";

    public static Long DEPT_ID, DEPT_ID2;

    @Before
    @Transactional
    public void setUp() {
        Department department = departmentService.findByName("Department");
        if (department == null) {
            department = new Department();
            department.setName("Department");
            Department add = departmentService.addDept(department);
            DEPT_ID = add.getId();
        } else {
            DEPT_ID = department.getId();
        }
        User user = new User();
        user.setUsername("User");
        user.setDepartmentId(DEPT_ID);
        user.setCreatedBy("Lucien");
        user.setModifiedBy("Lucien");
        user.setCreationDate(new Date());
        user.setModificationDate(new Date());
        user.setStatus("A");
        if (!userService.isUserExist(user)) {
            userService.addUser(user);
        }

        Department department2 = departmentService.findByName("Department2");
        if (department2 == null) {
            department2 = new Department();
            department2.setName("Department2");
            Department add = departmentService.addDept(department2);
            DEPT_ID2 = add.getId();
        } else {
            DEPT_ID2 = department2.getId();
        }
        User user2 = new User();
        user2.setUsername("User2");
        user2.setDepartmentId(DEPT_ID2);
        user2.setCreatedBy("Lucien2");
        user2.setModifiedBy("Lucien2");
        user2.setCreationDate(new Date());
        user2.setModificationDate(new Date());
        user2.setStatus("D");
        if (!userService.isUserExist(user2)) {
            userService.addUser(user2);
        }
    }


    @Test
    public void testCreateUserApiSuccess() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", "Lucien");
        requestBody.put("departmentId", DEPT_ID);
        requestBody.put("status", "A");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.POST, httpEntity, Object.class);

            HttpStatus httpStatus = apiResponse.getStatusCode();
            String responseBody = apiResponse.getBody().toString();
            logger.info("status code is: {}", httpStatus);
            logger.info("response body is: {}", responseBody);

            assertNotNull(apiResponse);
            assertEquals(HttpStatus.CREATED, httpStatus);

            User user = userService.findByUsername("Lucien");
            try {
                userService.deleteUser(user);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateUserApiDuplicate() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", "User");
        requestBody.put("departmentId", DEPT_ID);
        requestBody.put("status", "A");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.POST, httpEntity, Object.class);

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
    public void testCreateUserApiFieldError() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", "S");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.POST, httpEntity, Object.class);

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
    public void testUpdateUserApiSuccess() {
        User user = userService.findByUsername("User");
        Long id = user.getId();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", id);
        requestBody.put("departmentId", user.getDepartmentId());
        requestBody.put("username", "User");
        requestBody.put("status", "D");
        requestBody.put("createdBy", "Test2");
        requestBody.put("creationDate", getDateWithRightFormat(user.getCreationDate()));
        requestBody.put("modifiedBy", "Test2");
        requestBody.put("modificationDate", getDateWithRightFormat(new Date(System.currentTimeMillis())));
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.PUT, httpEntity, Object.class);

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
    public void testUpdateUserApiNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", -1);
        requestBody.put("username", "User");
        requestBody.put("departmentId", DEPT_ID);
        requestBody.put("status", "D");
        requestBody.put("createdBy", "Test2");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.PUT, httpEntity, Object.class);

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
    public void testUpdateUserApiFieldError() {
        Map<String, Object> requestBody = new HashMap<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.PUT, httpEntity, Object.class);

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
    public void testDeleteUserApiSuccess() {
        User user = userService.findByUsername("User");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", user.getId());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.DELETE, httpEntity, Object.class);

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
    public void testDeleteUserApiNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", -1);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.DELETE, httpEntity, Object.class);

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
    public void testGetUserApiSuccess() {
        User user = userService.findByUsername("User");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", user.getId());
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/" + user.getId(), HttpMethod.GET, httpEntity, Object.class);

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
    public void testGetUserApiNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", -1);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/" + -1, HttpMethod.GET, httpEntity, Object.class);

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
    public void testGetAllUserApi() {
        Map<String, Object> requestBody = new HashMap<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.GET, httpEntity, Object.class);

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
    public void testGetAllUserApiNull() {
        userService.deleteAllUser();
        Map<String, Object> requestBody = new HashMap<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> httpEntity =
                    new HttpEntity<>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);
            //Invoking the API
            ResponseEntity<Object> apiResponse =
                    restTemplate.exchange(BASE_URL + "user/", HttpMethod.GET, httpEntity, Object.class);

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
