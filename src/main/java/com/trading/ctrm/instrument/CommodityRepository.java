package com.trading.ctrm.instrument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Long> {
    Commodity findByName(String name);
}
