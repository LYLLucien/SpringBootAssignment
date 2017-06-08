package com.lucien.service;

import com.lucien.exception.NotFoundException;
import com.lucien.model.User;

import java.util.List;

/**
 * Created by Lucien on 2017/6/8.
 */
public interface IUserService {

    List<User> findAllUser();

    User findById(Long id);

    User findByUsername(String username);

    User addUser(User user);

    User updateUser(User user) throws NotFoundException;

    void deleteUser(User user) throws NotFoundException;

    void deleteAllUser();

    boolean isUserExist(User user);
}
