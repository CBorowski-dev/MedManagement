package de.medmanagement.rights;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";
    public static final List<String> ROLES = new ArrayList<String>() {
        {
            add(ADMIN);
            add(USER);
        }
    };

    public Role() {}

    public Role(String roleName) {
        this.roleName = roleName;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int roleId;
    private String roleName;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
