package com.example.coursemanager.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Courses",
        uniqueConstraints = @UniqueConstraint(name = "UQ_Course_Name_Category",
                columnNames = {"Name", "CourseCategoryId"}))
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Name", nullable = false, length = 200)
    private String name;

    @Column(name = "TuitionFee", nullable = false, precision = 15, scale = 2)
    private BigDecimal tuitionFee;

    @Column(name = "Duration", nullable = false)
    private Integer duration;

    @Column(name = "IsActive", nullable = false)
    private boolean active = true;

    @Column(name = "Description", length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CourseCategoryId", nullable = false)
    private CourseCategory courseCategory;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CourseImage> images = new ArrayList<>();

    // ====== Giá trị tính khi hiển thị, KHÔNG lưu vào database ======
    public String getDurationType() {
        if (duration == null) return "";
        return duration <= 30 ? "Ngắn hạn" : "Dài hạn";
    }

    public String getThumbnailUrl() {
        return images.stream()
                .filter(CourseImage::isThumbnail)
                .map(CourseImage::getImageUrl)
                .findFirst().orElse(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CourseCategory getCourseCategory() { return courseCategory; }
    public void setCourseCategory(CourseCategory courseCategory) { this.courseCategory = courseCategory; }
    public List<CourseImage> getImages() { return images; }
    public void setImages(List<CourseImage> images) { this.images = images; }
}
