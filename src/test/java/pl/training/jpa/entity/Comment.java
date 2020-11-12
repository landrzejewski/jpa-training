package pl.training.jpa.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Audited(targetAuditMode = NOT_AUDITED)
@Table(name = "comments")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Comment {

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
        if (!(otherObject instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
