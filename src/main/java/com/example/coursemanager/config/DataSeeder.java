package com.example.coursemanager.config;

import com.example.coursemanager.entity.Course;
import com.example.coursemanager.entity.CourseCategory;
import com.example.coursemanager.entity.CourseImage;
import com.example.coursemanager.repository.CourseCategoryRepository;
import com.example.coursemanager.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Tạo dữ liệu mẫu (3 loại, 6 khóa học) nếu database còn trống. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(CourseCategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        CourseCategory web = category("Lập trình Web", "Các khóa học phát triển ứng dụng web");
        CourseCategory mobile = category("Lập trình Mobile", "Các khóa học phát triển ứng dụng di động");
        CourseCategory design = category("Thiết kế đồ họa", "Các khóa học thiết kế và chỉnh sửa hình ảnh");

        course("ASP.NET Core", "4500000", 40, true, "Xây dựng web với ASP.NET Core MVC", web,
                "https://picsum.photos/seed/aspnet/600/400", "https://picsum.photos/seed/aspnet2/600/400");
        course("ReactJS", "3500000", 30, true, "Thư viện React cho frontend", web,
                "https://picsum.photos/seed/reactjs/600/400");
        course("Flutter Cơ bản", "3000000", 25, true, "Lập trình đa nền tảng với Flutter", mobile);   // chưa có ảnh
        course("Android Kotlin", "5000000", 60, false, "Lập trình Android với Kotlin", mobile,
                "https://picsum.photos/seed/kotlin/600/400");
        course("Photoshop Căn bản", "1800000", 20, true, "Chỉnh sửa ảnh với Photoshop", design,
                "https://picsum.photos/seed/photoshop/600/400");
        course("Illustrator Nâng cao", "2500000", 35, true, "Thiết kế vector chuyên sâu", design,
                "https://picsum.photos/seed/illustrator/600/400");
    }

    private CourseCategory category(String name, String desc) {
        CourseCategory c = new CourseCategory();
        c.setName(name);
        c.setDescription(desc);
        return categoryRepository.save(c);
    }

    /** Ảnh đầu tiên (nếu có) sẽ là ảnh đại diện. */
    private void course(String name, String fee, int duration, boolean active, String desc,
                        CourseCategory cat, String... imageUrls) {
        Course c = new Course();
        c.setName(name);
        c.setTuitionFee(new BigDecimal(fee));
        c.setDuration(duration);
        c.setActive(active);
        c.setDescription(desc);
        c.setCourseCategory(cat);
        for (int i = 0; i < imageUrls.length; i++) {
            CourseImage img = new CourseImage();
            img.setImageUrl(imageUrls[i]);
            img.setThumbnail(i == 0);
            img.setCourse(c);
            c.getImages().add(img);
        }
        courseRepository.save(c);
    }
}
