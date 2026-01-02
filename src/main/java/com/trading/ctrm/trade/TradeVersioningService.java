package com.trading.ctrm.trade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Trade Versioning Service - Endur-style amendment tracking
 * Creates snapshots of trade state for audit trail and reconstruction
 */
@Service
public class TradeVersioningService {

    private static final Logger log = LoggerFactory.getLogger(TradeVersioningService.class);

    private final TradeVersionRepository versionRepository;
    private final ObjectMapper objectMapper;

    public TradeVersioningService(TradeVersionRepository versionRepository) {
        this.versionRepository = versionRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // For LocalDate/LocalDateTime support
    }

    /**
     * Create initial version when trade is booked
     */
    @Transactional
    public TradeVersion createInitialVersion(Trade trade, String bookedBy) {
        log.info("Creating initial version for trade: {}", trade.getTradeId());

        try {
            String tradeSnapshot = objectMapper.writeValueAsString(trade);

            TradeVersion version = new TradeVersion(
                trade.getId(),
                1,
                "ORIGINAL",
                tradeSnapshot
            );
            version.setAmendedBy(bookedBy);
            version.setChangeDescription("Trade booked");

            return versionRepository.save(version);

        } catch (Exception e) {
            log.error("Failed to create initial version", e);
            throw new RuntimeException("Failed to create trade version", e);
        }
    }

    /**
     * Create amendment version
     */
    @Transactional
    public TradeVersion createAmendmentVersion(Trade trade, String amendedBy, String reason, String changeDiff) {
        log.info("Creating amendment version for trade: {}", trade.getTradeId());

        try {
            // Get current version number
            Integer currentVersion = versionRepository.countVersionsByTradeId(trade.getId());
            Integer newVersion = currentVersion + 1;

            String tradeSnapshot = objectMapper.writeValueAsString(trade);

            TradeVersion version = new TradeVersion(
                trade.getId(),
                newVersion,
                "AMENDMENT",
                tradeSnapshot
            );
            version.setAmendedBy(amendedBy);
            version.setAmendmentReason(reason);
            version.setChangeDiff(changeDiff);
            version.setChangeDescription("Trade amended");

            return versionRepository.save(version);

        } catch (Exception e) {
            log.error("Failed to create amendment version", e);
            throw new RuntimeException("Failed to create amendment version", e);
        }
    }

    /**
     * Create cancellation version
     */
    @Transactional
    public TradeVersion createCancellationVersion(Trade trade, String cancelledBy, String reason) {
        log.info("Creating cancellation version for trade: {}", trade.getTradeId());

        try {
            Integer currentVersion = versionRepository.countVersionsByTradeId(trade.getId());
            Integer newVersion = currentVersion + 1;

            String tradeSnapshot = objectMapper.writeValueAsString(trade);

            TradeVersion version = new TradeVersion(
                trade.getId(),
                newVersion,
                "CANCELLATION",
                tradeSnapshot
            );
            version.setAmendedBy(cancelledBy);
            version.setAmendmentReason(reason);
            version.setChangeDescription("Trade cancelled");

            return versionRepository.save(version);

        } catch (Exception e) {
            log.error("Failed to create cancellation version", e);
            throw new RuntimeException("Failed to create cancellation version", e);
        }
    }

    /**
     * Get all versions for a trade
     */
    public List<TradeVersion> getTradeHistory(Long tradeId) {
        return versionRepository.findByTradeIdOrderByVersionNumberAsc(tradeId);
    }

    /**
     * Get specific version
     */
    public Optional<TradeVersion> getVersion(Long tradeId, Integer versionNumber) {
        return versionRepository.findByTradeIdAndVersionNumber(tradeId, versionNumber);
    }

    /**
     * Get latest version
     */
    public Optional<TradeVersion> getLatestVersion(Long tradeId) {
        return versionRepository.findLatestVersion(tradeId);
    }

    /**
     * Reconstruct trade from version
     */
    public Trade reconstructTrade(Long tradeId, Integer versionNumber) {
        log.info("Reconstructing trade {} at version {}", tradeId, versionNumber);

        Optional<TradeVersion> versionOpt = versionRepository.findByTradeIdAndVersionNumber(tradeId, versionNumber);

        if (versionOpt.isEmpty()) {
            throw new RuntimeException("Version not found: " + tradeId + " v" + versionNumber);
        }

        try {
            TradeVersion version = versionOpt.get();
            return objectMapper.readValue(version.getTradeSnapshot(), Trade.class);
        } catch (Exception e) {
            log.error("Failed to reconstruct trade", e);
            throw new RuntimeException("Failed to reconstruct trade", e);
        }
    }

    /**
     * Get amendments by user
     */
    public List<TradeVersion> getAmendmentsByUser(String amendedBy) {
        return versionRepository.findByAmendedBy(amendedBy);
    }

    /**
     * Get amendments in date range
     */
    public List<TradeVersion> getAmendmentsInRange(LocalDateTime startDate, LocalDateTime endDate) {
        return versionRepository.findByAmendedAtBetween(startDate, endDate);
    }
}
