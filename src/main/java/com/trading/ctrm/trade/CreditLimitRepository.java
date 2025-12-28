package com.trading.ctrm.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CreditLimitRepository
        extends JpaRepository<CreditLimit, Long> {

    Optional<CreditLimit> findByCounterparty(String counterparty);
}
