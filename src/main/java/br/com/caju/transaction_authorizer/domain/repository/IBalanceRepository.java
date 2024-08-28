package br.com.caju.transaction_authorizer.domain.repository;

import br.com.caju.transaction_authorizer.domain.entity.Category;
import br.com.caju.transaction_authorizer.domain.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.math.BigDecimal;
import java.util.Optional;

public interface IBalanceRepository extends Repository<Balance, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Balance> findByAccountIdAndCategory(long accountId, Category category);

    @Modifying
    @Query("UPDATE Balance b SET b.totalAmount = :amount WHERE b.id = :id")
    int updateAmount(long id, BigDecimal amount);
}
