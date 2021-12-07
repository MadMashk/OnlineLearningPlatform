package model.dto;

public class StudentDTO {
    private String userName;
    private String name;
    private Long points;
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
