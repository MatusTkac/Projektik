package org.library.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.library.common.Displayable;
import org.library.common.Identifiable;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Identifiable, Displayable {
    private Long id;
    private String name;
    private String email;
    private LocalDate registeredAt;

    public String getContactInfo() {
        return name + " <" + email + ">";
    }

    @Override
    public String getDisplayInfo() {
        return "User{id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
