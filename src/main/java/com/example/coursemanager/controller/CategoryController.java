package com.example.coursemanager.controller;

import com.example.coursemanager.entity.CourseCategory;
import com.example.coursemanager.exception.BusinessException;
import com.example.coursemanager.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new CourseCategory());
        model.addAttribute("title", "Thêm loại khóa học");
        model.addAttribute("action", "/categories/create");
        return "categories/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("category") CourseCategory category, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("title", "Thêm loại khóa học");
            model.addAttribute("action", "/categories/create");
            return "categories/form";
        }
        categoryService.create(category);
        ra.addFlashAttribute("success", "Thêm loại khóa học thành công.");
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategory(id));
        model.addAttribute("title", "Sửa loại khóa học");
        model.addAttribute("action", "/categories/edit/" + id);
        return "categories/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("category") CourseCategory category,
                       BindingResult br, Model model, RedirectAttributes ra) {
        categoryService.getCategory(id); // 404 nếu không tồn tại
        if (br.hasErrors()) {
            model.addAttribute("title", "Sửa loại khóa học");
            model.addAttribute("action", "/categories/edit/" + id);
            return "categories/form";
        }
        categoryService.update(id, category);
        ra.addFlashAttribute("success", "Cập nhật loại khóa học thành công.");
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("success", "Đã xóa loại khóa học.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/categories";
    }
}
