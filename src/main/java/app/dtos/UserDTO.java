package app.dtos;
import lombok.*;
import app.entities.Role;
import app.entities.User;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private String email;
    private String password;
    private String name;
    private int phoneNumber;
    @ToString.Exclude
    private Set<String> roles;

    public UserDTO(String email, Set<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.phoneNumber = user.getPhoneNumber();
        setRoles(user.getRolesAsString());

    }


}