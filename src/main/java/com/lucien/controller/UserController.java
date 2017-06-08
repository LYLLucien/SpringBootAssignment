package com.lucien.controller;

import com.lucien.model.User;
import com.lucien.response.ResponseMsg;
import com.lucien.service.IUserService;
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
public class UserController extends BaseRestController {

    static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    IUserService userService;

    /**
     * Read All Department
     *
     * @return departments json, if empty return no content
     */
    @ResponseBody
    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUser() {
        List<User> users = userService.findAllUser();
        if (users.isEmpty()) {
            users = new ArrayList<>();
            return new ResponseEntity<>(users, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Read User by Id
     *
     * @return user json, if empty return error message
     */
    @ResponseBody
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        logger.info("Reading user with id {}", id);
        User user = userService.findById(id);
        if (user == null) {
            logger.error("User with id {} not found.", id);
            return new ResponseEntity(new ResponseMsg("User with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Add a user
     *
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@Valid @RequestBody User user, BindingResult result) {
        logger.info("Creating User : {}", user);
        List<FieldError> fieldErrors = result.getFieldErrors();
        if (fieldErrors.size() > 0) {
            logger.info("Creating User : {} has field errors", user);
            return new ResponseEntity<>(responseFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
        }
        if (userService.isUserExist(user)) {
            logger.error("Unable to create. A User with name {} already exist", user.getUsername());
            return new ResponseEntity(new ResponseMsg("Unable to create. A User with name " +
                    user.getUsername() + " already exist."), HttpStatus.CONFLICT);
        }
        User create = userService.addUser(user);
        return new ResponseEntity<>(create, HttpStatus.CREATED);
    }

    /**
     * update a user
     *
     * @param user update user
     * @return updated user or error message
     */
    @ResponseBody
    @RequestMapping(value = "/user/", method = RequestMethod.PUT)
    public ResponseEntity<?> editUser(@Valid @RequestBody User user, BindingResult result) {
        logger.info("Updating User with id {}", user.getId());
        List<FieldError> fieldErrors = result.getFieldErrors();
        if (fieldErrors.size() > 0) {
            logger.info("Editing User : {} has field errors", user);
            return new ResponseEntity<>(responseFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        User update = null;
        try {
            update = userService.updateUser(user);
        } catch (Exception e) {
            logger.error("Unable to update. User with id {} not found.", user.getId());
            return new ResponseEntity(new ResponseMsg("Unable to update. User with id " + user.getId() + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    /**
     * delete a user
     *
     * @param user
     * @return error message json if fail, empty json if success
     */
    @ResponseBody
    @RequestMapping(value = "/user/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestBody User user) {
        logger.info("Deleting User with id {}", user.getId());
        try {
            userService.deleteUser(user);
        } catch (Exception e) {
            logger.error("Unable to update. User with id {} not found.", user.getId());
            return new ResponseEntity(new ResponseMsg("Unable to delete. User with id " + user.getId() + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(new User(), HttpStatus.OK);
    }

}
