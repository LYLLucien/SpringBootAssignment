package com.lucien.controller;

import com.lucien.model.Department;
import com.lucien.response.ResponseMsg;
import com.lucien.service.IDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucien on 2017/6/8.
 */
@RestController
@RequestMapping("/api")
public class DepartmentController extends BaseRestController {

    static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    IDepartmentService departmentService;

    /**
     * Read All Department
     *
     * @return departments json, if empty return no content
     */
    @ResponseBody
    @RequestMapping(value = "/dept/", method = RequestMethod.GET)
    public ResponseEntity<List<Department>> listAllDept() {
        List<Department> departments = departmentService.findAllDept();
        if (departments.isEmpty()) {
            departments = new ArrayList<>();
            return new ResponseEntity<>(departments, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    /**
     * Read Department by Id
     *
     * @return department json, if empty return error message
     */
    @ResponseBody
    @RequestMapping(value = "/dept/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDept(@PathVariable("id") long id) {
        logger.info("Reading Dept with id {}", id);
        Department department = departmentService.findById(id);
        if (department == null) {
            logger.error("User with id {} not found.", id);
            return new ResponseEntity(new ResponseMsg("Department with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    /**
     * Add a department
     *
     * @param department
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/dept/", method = RequestMethod.POST)
    public ResponseEntity<?> addDept(@Valid @RequestBody Department department, BindingResult result) {
        logger.info("Creating Department : {}", department);
        List<FieldError> fieldErrors = result.getFieldErrors();
        if (fieldErrors.size() > 0) {
            logger.info("Creating Department : {} has field errors", department);
            return new ResponseEntity<>(responseFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        if (departmentService.isDeptExist(department)) {
            logger.error("Unable to create. A Department with name {} already exist", department.getName());
            return new ResponseEntity(new ResponseMsg("Unable to create. A Department with name " +
                    department.getName() + " already exist."), HttpStatus.CONFLICT);
        }
        Department create = departmentService.addDept(department);
        return new ResponseEntity<>(create, HttpStatus.CREATED);
    }

    /**
     * update a department
     *
     * @param department update department
     * @return updated department or error message
     */
    @ResponseBody
    @RequestMapping(value = "/dept/", method = RequestMethod.PUT)
    public ResponseEntity<?> editDept(@Valid @RequestBody Department department, BindingResult result) {
        logger.info("Updating Department with id {}", department.getId());
        List<FieldError> fieldErrors = result.getFieldErrors();
        if (fieldErrors.size() > 0) {
            logger.info("Editing Department : {} has field errors", department);
            return new ResponseEntity<>(responseFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
        }
        Department update = null;
        try {
            update = departmentService.updateDept(department);
        } catch (Exception e) {
            logger.error("Unable to update. Department with id {} not found.", department.getId());
            return new ResponseEntity(new ResponseMsg("Unable to update. Department with id " + department.getId() + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    /**
     * delete a department
     *
     * @param department
     * @return error message json if fail, empty json if success
     */
    @ResponseBody
    @RequestMapping(value = "/dept/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDept(@RequestBody Department department) {
        logger.info("Deleting Department with id {}", department.getId());
        try {
            departmentService.deleteDept(department);
        } catch (Exception e) {
            logger.error("Unable to update. Department with id {} not found.", department.getId());
            return new ResponseEntity(new ResponseMsg("Unable to delete. Department with id " + department.getId() + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(new Department(), HttpStatus.OK);
    }

}
