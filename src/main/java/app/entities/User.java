package app.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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


    @ManyToMany (mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    @JsonBackReference
    private Set<Event> events = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JsonManagedReference
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


    public void addEvent(Event event) {
        if(event != null){
            events.add(event);
            if(!event.getUsers().contains(this)){
                event.addUser(this);
            }
        }
    }
}