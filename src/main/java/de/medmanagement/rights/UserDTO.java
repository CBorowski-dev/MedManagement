package de.medmanagement.rights;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UserDTO {

    public Integer id;
    public String firstname;
    public String lastname;
    public String email;
    public String name;
    public String password;
    public boolean disabled;
    public boolean accountExpired;
    public boolean accountLocked;
    public boolean credentialsExpired;
    public String rolename;

    public UserDTO() {}

    public UserDTO(User user) {
        id = user.getId();
        firstname = user.getFirstname();
        lastname = user.getLastname();
        email = user.getEmail();
        name = user.getName();
        password = user.getPassword();
        disabled = user.isDisabled();
        accountExpired = user.isAccountExpired();
        accountLocked = user.isAccountLocked();
        credentialsExpired = user.isCredentialsExpired();
        rolename = ((Role) user.getRoles().get(0)).getRoleName();
    }

}
