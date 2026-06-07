package org.library.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBasedUserDao implements UserDao {
    private final Path usersFile;
    private final List<User> users = new ArrayList<>();

    public FileBasedUserDao(Path usersFile) {
        this.usersFile = usersFile;
        createFileIfMissing();
        loadUsers();
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    @Override
    public void saveUsers() {
        List<String> lines = users.stream()
                .map(user -> user.getId() + "|" +
                        user.getName() + "|" +
                        user.getEmail() + "|" +
                        user.getRegisteredAt())
                .toList();

        writeLines(lines);
    }

    @Override
    public Long getNextUserId() {
        return users.stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    private void loadUsers() {
        readLines().forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 4) {
                users.add(new User(
                        Long.parseLong(parts[0]),
                        parts[1],
                        parts[2],
                        LocalDate.parse(parts[3])
                ));
            }
        });
    }

    private void createFileIfMissing() {
        try {
            if (usersFile.getParent() != null) {
                Files.createDirectories(usersFile.getParent());
            }

            if (!Files.exists(usersFile)) {
                Files.createFile(usersFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create users file: " + usersFile, e);
        }
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(usersFile)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read users file: " + usersFile, e);
        }
    }

    private void writeLines(List<String> lines) {
        try {
            Files.write(
                    usersFile,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write users file: " + usersFile, e);
        }
    }
}