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

    private String newPassword;
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

    public UserDTO(String email, String password, String newPassword) {
        this.email = email;
        this.password = password;
        this.newPassword = newPassword;
    }

    public UserDTO(String email, String name, Set<String> roles) {
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.phoneNumber = user.getPhoneNumber();
        setRoles(user.getRolesAsString());

    }

    public UserDTO(String email, String password, String name, int phoneNumber, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public boolean hasRole(String role) {
        //This is to make make all letter cases work, this culd be simplere iff the stat of both strings are known
        return roles.stream().map(r -> r.equalsIgnoreCase(role)).reduce(false, (acc, r) -> acc = acc || r);
    }


}