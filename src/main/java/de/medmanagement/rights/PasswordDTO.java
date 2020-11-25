package de.medmanagement.rights;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PasswordDTO {

    public String oldPassword;
    public String newPassword;
    public String newPasswordConfirmation;

}
