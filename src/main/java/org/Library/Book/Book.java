package org.Library.Book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.Library.Common.Displayable;
import org.Library.Common.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book implements Identifiable, Displayable {
    private Long id;
    private String title;
    private Integer publishYear;
    private Author author;
    private Category category;
    private boolean available = true;

    public void borrowBook() {
        if (!available) {
            throw new IllegalStateException("Book is already borrowed: " + title);
        }

        available = false;
    }

    public void returnBook() {
        available = true;
    }

    @Override
    public String getDisplayInfo() {
        String authorName = author == null ? "Unknown Author" : author.getFullName();
        String categoryName = category == null ? "Uncategorized" : category.getName();

        return "Book{id=" + id +
                ", title='" + title + '\'' +
                ", publishYear=" + publishYear +
                ", author='" + authorName + '\'' +
                ", category='" + categoryName + '\'' +
                ", available=" + available +
                '}';
    }
}
