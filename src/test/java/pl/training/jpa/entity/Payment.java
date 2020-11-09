package pl.training.jpa.entity;

import lombok.*;
import lombok.extern.java.Log;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.javamoney.moneta.FastMoney;
import pl.training.jpa.common.CommonEntityLogger;
import pl.training.jpa.common.Identifiable;
import pl.training.jpa.converters.FastMoneyConverter;
import pl.training.jpa.converters.FastMoneyUserType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@TypeDef(name = "fastMoney", typeClass = FastMoneyUserType.class, defaultForType = FastMoney.class)
@Table(name = "payments", indexes = @Index(name = "payments_time_stamp", columnList = "time_stamp")
    //,uniqueConstraints = @UniqueConstraint(columnNames = "time_stamp")
)
@ExcludeDefaultListeners
//@ExcludeSuperclassListeners
//@EntityListeners(CommonEntityLogger.class)
@Entity
@Log
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Identifiable<Long> {

    @GeneratedValue
    @Id
    private Long id;
    @Column(name = "time_stamp", nullable = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Columns(columns = { @Column(name = "value"), @Column(name = "currency", length = 3) })
    //@Type(type = "fastMoney")
    //@Convert(converter = FastMoneyConverter.class)
    private FastMoney value;

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

    @PostLoad
    public void postLoad() {
        log.info(getClass().getSimpleName() + " PostLoad");
    }

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
        log.info(getClass().getSimpleName() + " PrePersist");
    }

    @PostPersist
    public void postPersist() {
        log.info(getClass().getSimpleName() + " PostPersist");
    }

    @PreUpdate
    public void preUpdate() {
        log.info(getClass().getSimpleName() + " PreUpdate");
    }

    @PostUpdate
    public void postUpdate() {
        log.info(getClass().getSimpleName() + " PostUpdate");
    }

    @PreRemove
    public void preRemove() {
        log.info(getClass().getSimpleName() + " PreRemove");
    }

    @PostRemove
    public void postRemove() {
        log.info(getClass().getSimpleName() + " PostRemove");
    }

}
