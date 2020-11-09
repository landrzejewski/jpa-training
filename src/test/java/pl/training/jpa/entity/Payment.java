package pl.training.jpa.entity;

import lombok.*;
import pl.training.jpa.common.Identifiable;

import javax.persistence.*;
import java.util.Date;

@Table(name = "payments", indexes = @Index(name = "payments_time_stamp", columnList = "time_stamp")
    //,uniqueConstraints = @UniqueConstraint(columnNames = "time_stamp")
)
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Identifiable<Long> {

    @GeneratedValue
    @Id
    private Long id;
    @Column(name = "time_stamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Payment)) {
            return false;
        }
        Payment other = (Payment) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
