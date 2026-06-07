package org.Library.book;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    List<Category> getCategories();

    void addCategory(Category category);

    void saveCategories();

    Long getNextCategoryId();

    Optional<Category> findCategoryById(Long id);
}
