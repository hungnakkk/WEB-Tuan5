package com.example.coursemanager.repository;

import com.example.coursemanager.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    long countByCourseCategory_Id(Long categoryId);

    boolean existsByNameIgnoreCaseAndCourseCategory_Id(String name, Long categoryId);

    boolean existsByNameIgnoreCaseAndCourseCategory_IdAndIdNot(String name, Long categoryId, Long id);
}
