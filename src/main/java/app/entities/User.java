package app.entities;


import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "users")
//@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE FROM User"))
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String password;

    private String name;

    private int phoneNumber;


    @ManyToMany (mappedBy = "users")
    @ToString.Exclude
    private Set<Event> events = new HashSet<>();

    @ManyToMany
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();


    public User(String email, String password, String name, int phoneNumber) {
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.name = name;
        this.phoneNumber = phoneNumber;
    }


    public boolean verifyPassword(String passwordToCheck){

        return BCrypt.checkpw(passwordToCheck, this.password);
    }


    public Set<String> getRolesAsString(){

        if (roles.isEmpty()) {
            return null;
        }
        Set<String> rolesAsStrings = new HashSet<>();
        roles.forEach((role) -> {
            rolesAsStrings.add(role.getName());
        });
        return rolesAsStrings;
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }
}