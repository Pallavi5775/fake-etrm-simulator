package com.trading.ctrm.deals;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trading.ctrm.instrument.Instrument;

@Repository
public interface DealTemplateRepository extends JpaRepository<DealTemplate, Long> {

    DealTemplate findByTemplateName(String templateName);

    Optional<DealTemplate> findByInstrument(Instrument instrument);
}