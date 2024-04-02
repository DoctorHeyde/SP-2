package app.persistance;

import app.entities.Role;
import app.entities.User;
import app.exceptions.EntityNotFoundException;

public interface IUserDAO {
    public User createUser(String username, String password);
    public User verifyUser(String username, String password) throws EntityNotFoundException;
    public User addRoleToUser(String username, String role) throws EntityNotFoundException;
    public Role createRole(String role);


}