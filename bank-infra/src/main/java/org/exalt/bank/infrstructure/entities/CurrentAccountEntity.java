package org.exalt.bank.infrstructure.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.exalt.bank.domain.model.CurrentAccount;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

    public CurrentAccount toSavingAccount(CurrentAccount.Builder builder) {
        return this.toBankAccount(builder).withOverdraftLimit(this.overdraftLimit).build();
    }
}
