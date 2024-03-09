package org.exalt.bank.infrastructure.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccountEntity extends BankAccountEntity {
    private BigDecimal overdraftLimit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentAccountEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(overdraftLimit, that.overdraftLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), overdraftLimit);
    }

    @Override
    public String toString() {
        return "CurrentAccountEntity{" +
                "overdraftLimit=" + overdraftLimit +
                '}' + super.toString();
    }
}
