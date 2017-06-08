package com.lucien.service.impl;

import com.lucien.exception.NotFoundException;
import com.lucien.model.Department;
import com.lucien.repositories.DepartmentRepository;
import com.lucien.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Lucien on 2017/6/8.
 */
@Service("departmentService")
@Transactional
public class DepartmentService implements IDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAllDept() {
        return departmentRepository.findAll();
    }

    @Override
    public Department findById(Long id) {
        return departmentRepository.findOne(id);
    }

    @Override
    public Department findByName(String name) {
        return departmentRepository.findByName(name);
    }

    @Override
    public Department addDept(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDept(Department department) throws NotFoundException {
        Department update = departmentRepository.findOne(department.getId());
        if (update == null) {
            throw new NotFoundException();
        }
        if (department.getId() != null) {
            update.setId(department.getId());
        }
        if (!StringUtils.isEmpty(department.getName())) {
            update.setName(department.getName());
        }
        if (!StringUtils.isEmpty(department.getCreatedBy())) {
            update.setCreatedBy(department.getCreatedBy());
        }
        if (department.getCreationDate() != null) {
            update.setCreationDate(department.getCreationDate());
        }
        if (!StringUtils.isEmpty(department.getModifiedBy())) {
            update.setModifiedBy(department.getModifiedBy());
        }
        if (department.getModificationDate() != null) {
            update.setModificationDate(department.getModificationDate());
        }
        if (!StringUtils.isEmpty(department.getStatus())) {
            update.setStatus(department.getStatus());
        }
        return departmentRepository.save(update);
    }

    @Override
    public void deleteDept(Department department) throws NotFoundException {
        departmentRepository.delete(department);
    }

    @Override
    public void deleteAllDept() {
        departmentRepository.deleteAll();
    }

    @Override
    public boolean isDeptExist(Department department) {
        return departmentRepository.findByName(department.getName()) != null;
    }
}
