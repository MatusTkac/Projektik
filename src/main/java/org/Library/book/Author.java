package org.Library.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.Library.common.Displayable;
import org.Library.common.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author implements Identifiable, Displayable {
    private Long id;
    private String firstName;
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getDisplayInfo() {
        return "Author{id=" + id + ", fullName='" + getFullName() + "'}";
    }
}
