package com.lucien.service.impl;

import com.lucien.exception.NotFoundException;
import com.lucien.model.User;
import com.lucien.repositories.UserRepository;
import com.lucien.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Lucien on 2017/6/8.
 */
@Service("userService")
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) throws NotFoundException {
        User update = userRepository.findOne(user.getId());
        if (update == null) {
            throw new NotFoundException();
        }
        if (user.getId() != null) {
            update.setId(user.getId());
        }
        if (user.getDepartmentId() != null) {
            update.setDepartmentId(user.getDepartmentId());
        }
        if (!StringUtils.isEmpty(user.getUsername())) {
            update.setUsername(user.getUsername());
        }
        if (!StringUtils.isEmpty(user.getCreatedBy())) {
            update.setCreatedBy(user.getCreatedBy());
        }
        if (user.getCreationDate() != null) {
            update.setCreationDate(user.getCreationDate());
        }
        if (!StringUtils.isEmpty(user.getModifiedBy())) {
            update.setModifiedBy(user.getModifiedBy());
        }
        if (user.getModificationDate() != null) {
            update.setModificationDate(user.getModificationDate());
        }
        if (!StringUtils.isEmpty(user.getStatus())) {
            update.setStatus(user.getStatus());
        }
        return userRepository.save(update);
    }

    @Override
    public void deleteUser(User user) throws NotFoundException {
        userRepository.delete(user);
    }

    @Override
    public void deleteAllUser() {
        userRepository.deleteAll();
    }

    @Override
    public boolean isUserExist(User user) {
        return userRepository.findByUsername(user.getUsername()) != null;
    }
}
