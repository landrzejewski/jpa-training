package pl.training.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Table(name = "post_details")
@Entity
@Getter
@Setter
public class PostDetails {

    @GeneratedValue
    @Id
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date created = new Date();
    @Column(name = "created_by")
    private String createdBy;
    @OneToOne(fetch = FetchType.LAZY)
    private Post post;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof PostDetails)) {
            return false;
        }
        PostDetails other = (PostDetails) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
