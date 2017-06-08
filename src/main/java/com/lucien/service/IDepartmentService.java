package com.lucien.service;

import com.lucien.exception.NotFoundException;
import com.lucien.model.Department;

import java.util.List;

/**
 * Created by Lucien on 2017/6/8.
 */
public interface IDepartmentService {

    List<Department> findAllDept();

    Department findById(Long id);

    Department findByName(String name);

    Department addDept(Department department);

    Department updateDept(Department department) throws NotFoundException;

    void deleteDept(Department department) throws NotFoundException;

    void deleteAllDept();

    boolean isDeptExist(Department department);
}
