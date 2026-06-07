package org.library.user;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> getUsers();

    void addUser(User user);

    void saveUsers();

    Long getNextUserId();

    Optional<User> findUserById(Long id);
}