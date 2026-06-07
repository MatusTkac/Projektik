package org.Library.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBasedAuthorDao implements AuthorDao {
    private final Path authorsFile;
    private final List<Author> authors = new ArrayList<>();

    public FileBasedAuthorDao(Path authorsFile) {
        this.authorsFile = authorsFile;
        createFileIfMissing();
        loadAuthors();
    }

    @Override
    public List<Author> getAuthors() {
        return authors;
    }

    @Override
    public void addAuthor(Author author) {
        authors.add(author);
        saveAuthors();
    }

    @Override
    public void saveAuthors() {
        List<String> lines = authors.stream()
                .map(author -> author.getId() + "|" + author.getFirstName() + "|" + author.getLastName())
                .toList();

        writeLines(lines);
    }

    @Override
    public Long getNextAuthorId() {
        return authors.stream()
                .map(Author::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    @Override
    public Optional<Author> findAuthorById(Long id) {
        return authors.stream()
                .filter(author -> author.getId().equals(id))
                .findFirst();
    }

    private void loadAuthors() {
        readLines().forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 3) {
                authors.add(new Author(
                        Long.parseLong(parts[0]),
                        parts[1],
                        parts[2]
                ));
            }
        });
    }

    private void createFileIfMissing() {
        try {
            if (authorsFile.getParent() != null) {
                Files.createDirectories(authorsFile.getParent());
            }

            if (!Files.exists(authorsFile)) {
                Files.createFile(authorsFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create authors file: " + authorsFile, e);
        }
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(authorsFile)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read authors file: " + authorsFile, e);
        }
    }

    private void writeLines(List<String> lines) {
        try {
            Files.write(
                    authorsFile,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write authors file: " + authorsFile, e);
        }
    }
}
