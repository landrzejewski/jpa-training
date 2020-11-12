package pl.training.cdi;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class Account {

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private Long balance;
    @NonNull
    @Column(name = "account_number", unique = true)
    private String number;

}
