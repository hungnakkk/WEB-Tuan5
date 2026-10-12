# Course Manager - Spring Boot + Thymeleaf + JPA (MySQL)

Xem hướng dẫn chạy chi tiết trong câu trả lời / bên dưới.

1. Cài JDK 17+ và Maven (hoặc dùng IntelliJ IDEA / Eclipse / VS Code mở thư mục này).
2. Sửa username/password MySQL trong `src/main/resources/application.properties`.
3. Chạy:  `mvn spring-boot:run`
4. Mở:  http://localhost:8080

Chạy không cần MySQL (database H2 trong bộ nhớ):
`mvn spring-boot:run -Dspring-boot.run.profiles=h2`
Truy cập H2 console: http://localhost:8080/h2-console  (JDBC URL: jdbc:h2:mem:coursedb, user: sa, không mật khẩu)
