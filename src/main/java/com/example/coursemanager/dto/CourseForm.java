package com.example.coursemanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CourseForm {

    private Long id;

    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Học phí phải lớn hơn 0")
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    private Integer duration;

    private boolean active = true;

    private String description;

    @NotNull(message = "Vui lòng chọn loại khóa học")
    private Long courseCategoryId;

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
    public Long getCourseCategoryId() { return courseCategoryId; }
    public void setCourseCategoryId(Long courseCategoryId) { this.courseCategoryId = courseCategoryId; }
}
