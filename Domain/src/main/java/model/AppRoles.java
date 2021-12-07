package model;

import model.constants.RolesEnumeration;

import javax.persistence.*;

@Entity
@Table(name ="approles")
public class AppRoles {
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "sequencerole", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @Column(name = "id")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "name")
    private RolesEnumeration name;

    public AppRoles() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RolesEnumeration getName() {
        return name;
    }

    public void setName(RolesEnumeration name) {
        this.name = name;
    }
}
