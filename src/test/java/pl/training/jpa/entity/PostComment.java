package pl.training.jpa.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "post_comments")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class PostComment {

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String text;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof PostComment)) {
            return false;
        }
        PostComment other = (PostComment) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
