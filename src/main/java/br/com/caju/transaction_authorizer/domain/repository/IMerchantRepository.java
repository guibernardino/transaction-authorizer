package br.com.caju.transaction_authorizer.domain.repository;

import br.com.caju.transaction_authorizer.domain.entity.Merchant;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface IMerchantRepository extends Repository<Merchant, Long> {
    Optional<Merchant> findByName(String name);
}
