-- (TUỲ CHỌN) Chỉ cần nếu bạn muốn tự tạo DB/bảng bằng tay.
-- Mặc định ứng dụng TỰ tạo database + bảng + dữ liệu mẫu khi chạy.
CREATE DATABASE IF NOT EXISTS course_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_db;

CREATE TABLE IF NOT EXISTS CourseCategories (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(150) NOT NULL,
    Description VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS Courses (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(200) NOT NULL,
    TuitionFee DECIMAL(15,2) NOT NULL,
    Duration INT NOT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    Description VARCHAR(2000),
    CourseCategoryId BIGINT NOT NULL,
    CONSTRAINT UQ_Course_Name_Category UNIQUE (Name, CourseCategoryId),
    CONSTRAINT FK_Course_Category FOREIGN KEY (CourseCategoryId) REFERENCES CourseCategories(Id)
);

CREATE TABLE IF NOT EXISTS CourseImages (
    Id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ImageUrl VARCHAR(500) NOT NULL,
    IsThumbnail BIT NOT NULL DEFAULT 0,
    CourseId BIGINT NOT NULL,
    CONSTRAINT FK_Image_Course FOREIGN KEY (CourseId) REFERENCES Courses(Id)
);
