package com.example.coursemanager.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model, HttpServletResponse resp) {
        resp.setStatus(404);
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleBadParam(MethodArgumentTypeMismatchException ex, Model model, HttpServletResponse resp) {
        resp.setStatus(400);
        model.addAttribute("message", "Tham số '" + ex.getName() + "' không hợp lệ.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleOther(Exception ex, Model model, HttpServletResponse resp) {
        resp.setStatus(500);
        model.addAttribute("message", "Đã có lỗi xảy ra, vui lòng thử lại sau.");
        return "error";
    }
}
