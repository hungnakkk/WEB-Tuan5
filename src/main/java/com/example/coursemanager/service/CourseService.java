package com.example.coursemanager.service;

import com.example.coursemanager.dto.CourseForm;
import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.CourseCategory;
import com.example.coursemanager.entity.CourseImage;
import com.example.coursemanager.exception.BusinessException;
import com.example.coursemanager.exception.NotFoundException;
import com.example.coursemanager.repository.CourseCategoryRepository;
import com.example.coursemanager.repository.CourseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CourseCategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    // ================= TÌM KIẾM / LỌC / SẮP XẾP (xử lý trong database) =================
    @Transactional(readOnly = true)
    public List<Course> search(String keyword, Long categoryId, String status,
                               BigDecimal minFee, BigDecimal maxFee, String sort) {

        Specification<Course> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isBlank()) {
            final String pattern = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.<String>get("name")), pattern));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("courseCategory").get("id"), categoryId));
        }
        if ("active".equals(status)) {
            spec = spec.and((root, query, cb) -> cb.isTrue(root.<Boolean>get("active")));
        } else if ("inactive".equals(status)) {
            spec = spec.and((root, query, cb) -> cb.isFalse(root.<Boolean>get("active")));
        }
        if (minFee != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("tuitionFee"), minFee));
        }
        if (maxFee != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("tuitionFee"), maxFee));
        }

        Sort order;
        if ("fee_asc".equals(sort)) {
            order = Sort.by(Sort.Direction.ASC, "tuitionFee");
        } else if ("fee_desc".equals(sort)) {
            order = Sort.by(Sort.Direction.DESC, "tuitionFee");
        } else if ("duration_asc".equals(sort)) {
            order = Sort.by(Sort.Direction.ASC, "duration");
        } else {
            order = Sort.by(Sort.Direction.ASC, "id");
        }
        return courseRepository.findAll(spec, order);
    }

    @Transactional(readOnly = true)
    public Course getCourse(Long id) {
        if (id == null) throw new NotFoundException("Không tìm thấy khóa học.");
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học có Id = " + id));
    }

    @Transactional(readOnly = true)
    public CourseForm toForm(Course c) {
        CourseForm f = new CourseForm();
        f.setId(c.getId());
        f.setName(c.getName());
        f.setTuitionFee(c.getTuitionFee());
        f.setDuration(c.getDuration());
        f.setActive(c.isActive());
        f.setDescription(c.getDescription());
        f.setCourseCategoryId(c.getCourseCategory().getId());
        return f;
    }

    // ================= VALIDATION NGHIỆP VỤ =================
    @Transactional(readOnly = true)
    public void validateBusiness(CourseForm f, Long excludeId, BindingResult br) {
        boolean categoryOk = false;
        if (f.getCourseCategoryId() != null) {
            if (!categoryRepository.existsById(f.getCourseCategoryId())) {
                br.rejectValue("courseCategoryId", "notfound", "Loại khóa học không tồn tại");
            } else {
                categoryOk = true;
            }
        }
        if (categoryOk && f.getName() != null && !f.getName().isBlank() && !br.hasFieldErrors("name")) {
            String name = f.getName().trim();
            boolean dup = (excludeId == null)
                    ? courseRepository.existsByNameIgnoreCaseAndCourseCategory_Id(name, f.getCourseCategoryId())
                    : courseRepository.existsByNameIgnoreCaseAndCourseCategory_IdAndIdNot(name, f.getCourseCategoryId(), excludeId);
            if (dup) {
                br.rejectValue("name", "duplicate", "Tên khóa học đã tồn tại trong loại khóa học này");
            }
        }
    }

    public Course save(CourseForm f, Long id) {
        CourseCategory category = categoryRepository.findById(f.getCourseCategoryId())
                .orElseThrow(() -> new NotFoundException("Loại khóa học không tồn tại"));
        Course c = (id == null) ? new Course() : getCourse(id);
        c.setName(f.getName().trim());
        c.setTuitionFee(f.getTuitionFee());
        c.setDuration(f.getDuration());
        c.setActive(f.isActive());
        c.setDescription(f.getDescription());
        c.setCourseCategory(category);
        return courseRepository.save(c);
    }

    // ================= QUẢN LÝ ẢNH =================
    public void addImage(Long courseId, String imageUrl, boolean thumbnail) {
        Course course = getCourse(courseId);
        String url = imageUrl == null ? "" : imageUrl.trim();
        if (url.isEmpty()) {
            throw new BusinessException("Đường dẫn ảnh không được để trống.");
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new BusinessException("Đường dẫn ảnh phải bắt đầu bằng http:// hoặc https://");
        }
        if (url.length() > 500) {
            throw new BusinessException("Đường dẫn ảnh quá dài (tối đa 500 ký tự).");
        }
        if (thumbnail) {
            course.getImages().forEach(i -> i.setThumbnail(false)); // chỉ 1 ảnh đại diện
        }
        CourseImage img = new CourseImage();
        img.setImageUrl(url);
        img.setThumbnail(thumbnail);
        img.setCourse(course);
        course.getImages().add(img);
        courseRepository.save(course);
    }

    public void setThumbnail(Long courseId, Long imageId) {
        Course course = getCourse(courseId);
        boolean found = course.getImages().stream().anyMatch(i -> i.getId().equals(imageId));
        if (!found) {
            throw new NotFoundException("Không tìm thấy ảnh có Id = " + imageId + " trong khóa học này");
        }
        // ảnh đại diện cũ tự động thành ảnh thường
        for (CourseImage i : course.getImages()) {
            i.setThumbnail(i.getId().equals(imageId));
        }
        courseRepository.save(course);
    }

    public void deleteImage(Long courseId, Long imageId) {
        Course course = getCourse(courseId);
        CourseImage img = course.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy ảnh có Id = " + imageId + " trong khóa học này"));
        course.getImages().remove(img);
        courseRepository.save(course);
    }
}
