package pl.training.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "post_comments")
@Entity
@Getter
@Setter
public class PostComment {

    @GeneratedValue
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    private String text;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Payment)) {
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
