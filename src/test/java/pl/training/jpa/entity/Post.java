package pl.training.jpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NamedQuery(name = Post.REST_TEXT, query = "update Post p set p.text = 'test'")
@Table(name = "posts")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Post {

    public static final String REST_TEXT = "postRestText";

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String text;
    //@BatchSize(size = 5)
    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    /*
    @ManyToMany
    private List<Tag> tags = new ArrayList<>();
    */

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Post)) {
            return false;
        }
        Post other = (Post) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
