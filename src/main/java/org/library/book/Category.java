package org.library.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.library.common.Displayable;
import org.library.common.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Identifiable, Displayable {
    private Long id;
    private String name;

    @Override
    public String getDisplayInfo() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
