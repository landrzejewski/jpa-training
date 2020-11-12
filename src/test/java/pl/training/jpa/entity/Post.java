package pl.training.jpa.entity;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Cacheable
@org.hibernate.annotations.Cache(region = "posts", usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
@NamedEntityGraph(
        name = Post.EAGER_COMMENTS,
        attributeNodes = {
                @NamedAttributeNode(value = "comments")
                /*,subgraph = @NamedSubgraph(
                        name = "name",
                        attributeNodes = {
                               @NamedAttributeNode("name")
                        }
                ))*/
        }
)
@NamedStoredProcedureQuery(
        name = "ourName",
        procedureName = "procedureName",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, type = Double.class, name = "parameterName")
        }
)
@SqlResultSetMapping(
        name = "postMapping",
        classes = @ConstructorResult(
                targetClass = PostLite.class,
                columns = {
                        @ColumnResult(name = "title")
                }
        )
)
@NamedNativeQuery(name = Post.BY_ID_NATIVE, query = "select * from posts p where id = ?")
@NamedQuery(name = Post.REST_TEXT, query = "update Post p set p.text = 'test'"
/*        , hints = {
        @QueryHint(name = "javax.persistence.cache.retrieveMode", value = "USE"),
        @QueryHint(name = "javax.persistence.cache.storeMode", value = "REFRESH")
}*/
)
@Table(name = "posts")
@Entity
@Getter
@Setter
@ToString(exclude = {"comments", "tags"})
@RequiredArgsConstructor
@NoArgsConstructor
public class Post {

    public static final String REST_TEXT = "postRestText";
    public static final String BY_ID_NATIVE = "postByIdNative";
    public static final String EAGER_COMMENTS = "postEagerComments";

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String title;
    @Lob
    @Column(length = 2_000)
    @Basic(fetch = FetchType.LAZY)
    @NonNull
    private String text;
    @NotAudited
    @Lob
    @Column(length = 20_000)
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;
    //@BatchSize(size = 5)
    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    // Eager on many collections causes cartesian problem (records returned form db = number of posts * number of comments)
    @NotAudited
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

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
