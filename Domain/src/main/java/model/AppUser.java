package model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="appuser",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class AppUser {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequenceuser", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;
    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", schema = "senla",
            joinColumns = {@JoinColumn(name = "appuser_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "approles_id", referencedColumnName = "id")}
    )
    private Set<AppRoles> roles;

    public AppUser( String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public AppUser () {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<AppRoles> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRoles> roles) {
        this.roles = roles;
    }
}