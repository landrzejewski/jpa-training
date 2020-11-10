package pl.training.jpa.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tags")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Tag {

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String name;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
