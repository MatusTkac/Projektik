package org.library.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBasedCategoryDao implements CategoryDao {
    private final Path categoriesFile;
    private final List<Category> categories = new ArrayList<>();

    public FileBasedCategoryDao(Path categoriesFile) {
        this.categoriesFile = categoriesFile;
        createFileIfMissing();
        loadCategories();
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public void addCategory(Category category) {
        categories.add(category);
        saveCategories();
    }

    @Override
    public void saveCategories() {
        List<String> lines = categories.stream()
                .map(category -> category.getId() + "|" + category.getName())
                .toList();

        writeLines(lines);
    }

    @Override
    public Long getNextCategoryId() {
        return categories.stream()
                .map(Category::getId)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst();
    }

    private void loadCategories() {
        readLines().forEach(line -> {
            String[] parts = line.split("\\|");

            if (parts.length == 2) {
                categories.add(new Category(
                        Long.parseLong(parts[0]),
                        parts[1]
                ));
            }
        });
    }

    private void createFileIfMissing() {
        try {
            if (categoriesFile.getParent() != null) {
                Files.createDirectories(categoriesFile.getParent());
            }

            if (!Files.exists(categoriesFile)) {
                Files.createFile(categoriesFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create categories file: " + categoriesFile, e);
        }
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(categoriesFile)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read categories file: " + categoriesFile, e);
        }
    }

    private void writeLines(List<String> lines) {
        try {
            Files.write(
                    categoriesFile,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write categories file: " + categoriesFile, e);
        }
    }
}