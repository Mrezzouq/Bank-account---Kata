package org.exalt.bank.infrstructure.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.exalt.bank.domain.model.SavingAccount;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavingAccountEntity extends BankAccountEntity {
    private BigDecimal depositLimit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavingAccountEntity that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(depositLimit, that.depositLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), depositLimit);
    }

    @Override
    public String toString() {
        return "SavingAccountEntity{" +
                "depositLimit=" + depositLimit +
                '}' + super.toString();
    }

    public SavingAccount toSavingAccount(SavingAccount.Builder builder) {
        return this.toBankAccount(builder).withDepositLimit(this.depositLimit).build();
    }
}
