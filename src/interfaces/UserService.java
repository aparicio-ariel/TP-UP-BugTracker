package interfaces;

import model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    void createUser(User user);
    void updateUser(User user);
    void deleteUser(Long userId);
    User getUserByUsernameAndPassword(String username, String password);
}
