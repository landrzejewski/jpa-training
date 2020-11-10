package pl.training.jpa.entity;

import lombok.*;
import pl.training.jpa.validators.Description;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Table(name = "orders")
@Entity
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @GeneratedValue
    @Id
    private Long id;
    //@NotEmpty
    //@Pattern(regexp = "[a-z]+")
    private String owner;
    @Description(minLength = 3)
    private String description;
    private Integer totalValue;
    @Version
    private Long version;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Payment)) {
            return false;
        }
        Order other = (Order) otherObject;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
