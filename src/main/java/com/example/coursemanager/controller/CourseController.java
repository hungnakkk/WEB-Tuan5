package com.example.coursemanager.controller;

import com.example.coursemanager.dto.CourseForm;
import com.example.coursemanager.entity.Course;
import com.example.coursemanager.exception.BusinessException;
import com.example.coursemanager.service.CategoryService;
import com.example.coursemanager.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CategoryService categoryService;

    public CourseController(CourseService courseService, CategoryService categoryService) {
        this.courseService = courseService;
        this.categoryService = categoryService;
    }

    // ---------------- DANH SÁCH + TÌM KIẾM + LỌC + SẮP XẾP ----------------
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String minFee,
                       @RequestParam(required = false) String maxFee,
                       @RequestParam(required = false) String sort,
                       Model model) {

        List<String> warnings = new ArrayList<>();

        BigDecimal min = parseMoney(minFee, "Học phí tối thiểu", warnings);
        BigDecimal max = parseMoney(maxFee, "Học phí tối đa", warnings);
        if (min != null && max != null && min.compareTo(max) > 0) {
            warnings.add("Học phí tối thiểu không được lớn hơn học phí tối đa. Bộ lọc học phí đã được bỏ qua.");
            min = null;
            max = null;
        }
        Long catFilter = categoryId;
        if (catFilter != null && !categoryService.exists(catFilter)) {
            warnings.add("Loại khóa học được chọn không tồn tại. Bộ lọc loại đã được bỏ qua.");
            catFilter = null;
        }

        List<Course> courses = courseService.search(keyword, catFilter, status, min, max, sort);

        model.addAttribute("courses", courses);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("warnings", warnings);
        // giữ lại điều kiện đã nhập trên form
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("minFee", minFee);
        model.addAttribute("maxFee", maxFee);
        model.addAttribute("sort", sort);
        return "courses/list";
    }

    private BigDecimal parseMoney(String value, String label, List<String> warnings) {
        if (value == null || value.isBlank()) return null;
        try {
            BigDecimal v = new BigDecimal(value.trim());
            if (v.signum() < 0) {
                warnings.add(label + " không được âm. Đã bỏ qua.");
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            warnings.add(label + " không hợp lệ. Đã bỏ qua.");
            return null;
        }
    }

    // ---------------- CREATE ----------------
    @GetMapping("/create")
    public String createForm(Model model) {
        prepareForm(model, new CourseForm(), "Thêm khóa học", "/courses/create");
        return "courses/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("course") CourseForm form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        courseService.validateBusiness(form, null, br);
        if (br.hasErrors()) {
            prepareForm(model, null, "Thêm khóa học", "/courses/create");
            return "courses/form";
        }
        courseService.save(form, null);
        ra.addFlashAttribute("success", "Thêm khóa học thành công.");
        return "redirect:/courses";
    }

    // ---------------- EDIT ----------------
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Course c = courseService.getCourse(id);
        prepareForm(model, courseService.toForm(c), "Sửa khóa học", "/courses/edit/" + id);
        return "courses/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("course") CourseForm form,
                       BindingResult br, Model model, RedirectAttributes ra) {
        courseService.getCourse(id); // 404 nếu không tồn tại
        courseService.validateBusiness(form, id, br);
        if (br.hasErrors()) {
            prepareForm(model, null, "Sửa khóa học", "/courses/edit/" + id);
            return "courses/form";
        }
        courseService.save(form, id);
        ra.addFlashAttribute("success", "Cập nhật khóa học thành công.");
        return "redirect:/courses";
    }

    private void prepareForm(Model model, CourseForm form, String title, String action) {
        if (form != null) model.addAttribute("course", form);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", title);
        model.addAttribute("action", action);
    }

    // ---------------- DETAILS ----------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourse(id));
        return "courses/details";
    }

    // ---------------- ẢNH ----------------
    @PostMapping("/{id}/images")
    public String addImage(@PathVariable Long id,
                           @RequestParam(required = false) String imageUrl,
                           @RequestParam(defaultValue = "false") boolean thumbnail,
                           RedirectAttributes ra) {
        try {
            courseService.addImage(id, imageUrl, thumbnail);
            ra.addFlashAttribute("success", "Đã thêm ảnh.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/courses/details/" + id;
    }

    @PostMapping("/{id}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes ra) {
        courseService.setThumbnail(id, imageId);
        ra.addFlashAttribute("success", "Đã đổi ảnh đại diện.");
        return "redirect:/courses/details/" + id;
    }

    @PostMapping("/{id}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes ra) {
        courseService.deleteImage(id, imageId);
        ra.addFlashAttribute("success", "Đã xóa ảnh.");
        return "redirect:/courses/details/" + id;
    }
}
