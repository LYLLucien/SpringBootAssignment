package com.lucien.repositories;

import com.lucien.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Lucien on 2017/6/8.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Department findByName(String name);

}
