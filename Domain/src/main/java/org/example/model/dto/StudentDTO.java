package org.example.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "студент")
public class StudentDTO {
    @Schema(description = "имя пользователя", example = "Ванька227")
    private String userName;
    @Schema(description = "имя", example = "Иван Иванов")
    private String name;
    @Schema(description = "баллы пользователя", accessMode = Schema.AccessMode.READ_ONLY)
    private Long points;
    @Schema(description = "электронная почта", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String email;

    public StudentDTO(String userName, String name, Long points, String email) {
        this.userName = userName;
        this.name = name;
        this.points = points;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}