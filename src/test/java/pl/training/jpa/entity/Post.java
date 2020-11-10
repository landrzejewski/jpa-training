package pl.training.jpa.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "posts")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Post {

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String text;
    @BatchSize(size = 5)
    //@Fetch(FetchMode.SUBSELECT)
    @OneToMany(fetch = FetchType.LAZY)
    private List<PostComment> comments = new ArrayList<>();

    /*  @OneToOne(cascade = CascadeType.ALL, mappedBy = "post")
    private PostDetails postDetails;
    @ManyToMany
    private List<Tag> tags = new ArrayList<>();
    @Version
    private long version;*/

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
