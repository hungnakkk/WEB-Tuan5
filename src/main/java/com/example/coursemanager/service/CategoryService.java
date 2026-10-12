package com.example.coursemanager.service;

import com.example.coursemanager.entity.CourseCategory;
import com.example.coursemanager.exception.BusinessException;
import com.example.coursemanager.exception.NotFoundException;
import com.example.coursemanager.repository.CourseCategoryRepository;
import com.example.coursemanager.repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryService(CourseCategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseCategory> findAll() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return id != null && categoryRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public CourseCategory getCategory(Long id) {
        if (id == null) throw new NotFoundException("Không tìm thấy loại khóa học.");
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại khóa học có Id = " + id));
    }

    public void create(CourseCategory form) {
        CourseCategory c = new CourseCategory();
        c.setName(form.getName().trim());
        c.setDescription(form.getDescription());
        categoryRepository.save(c);
    }

    public void update(Long id, CourseCategory form) {
        CourseCategory c = getCategory(id);
        c.setName(form.getName().trim());
        c.setDescription(form.getDescription());
        categoryRepository.save(c);
    }

    /** Ràng buộc 1: không xóa loại khóa học nếu vẫn còn khóa học thuộc loại đó. */
    public void delete(Long id) {
        CourseCategory c = getCategory(id);
        long count = courseRepository.countByCourseCategory_Id(id);
        if (count > 0) {
            throw new BusinessException("Không thể xóa loại khóa học '" + c.getName()
                    + "' vì vẫn còn " + count + " khóa học thuộc loại này.");
        }
        categoryRepository.delete(c);
    }
}
