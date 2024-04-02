package app.persistance;

import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityNotFoundException;

import java.util.Set;

public interface IUserDAO {
    public User createUser(String email, String password, String name, int phoneNumber, Set<String> roles);
    public User verifyUser(String username, String password) throws EntityNotFoundException;
    public User addRoleToUser(String username, String role) throws EntityNotFoundException;
    public Role createRole(String role);


}