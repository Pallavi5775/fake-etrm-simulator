package com.trading.ctrm.trade;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Trade Version - Endur-style amendment tracking
 * Stores complete snapshots of trade state for audit and reconstruction
 */
@Entity
@Table(name = "trade_version")
public class TradeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "version_type", nullable = false, length = 30)
    private String versionType; // ORIGINAL, AMENDMENT, CANCELLATION

    // Complete trade snapshot stored as JSON
    @Column(columnDefinition = "TEXT", nullable = false)
    private String tradeSnapshot;

    // What changed
    @Column(columnDefinition = "TEXT")
    private String changeDescription;

    @Column(columnDefinition = "TEXT")
    private String changeDiff; // JSON diff of changes

    // Metadata
    @Column(name = "amended_by", length = 50)
    private String amendedBy;

    @Column(name = "amended_at", nullable = false)
    private LocalDateTime amendedAt;

    @Column(name = "amendment_reason", length = 500)
    private String amendmentReason;

    // Link to approval if applicable
    @Column(name = "approval_id")
    private Long approvalId;

    // Constructors
    public TradeVersion() {
        this.amendedAt = LocalDateTime.now();
    }

    public TradeVersion(Long tradeId, Integer versionNumber, String versionType, String tradeSnapshot) {
        this();
        this.tradeId = tradeId;
        this.versionNumber = versionNumber;
        this.versionType = versionType;
        this.tradeSnapshot = tradeSnapshot;
    }

    // Getters and Setters
    public Long getVersionId() { return versionId; }
    public void setVersionId(Long versionId) { this.versionId = versionId; }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getVersionType() { return versionType; }
    public void setVersionType(String versionType) { this.versionType = versionType; }

    public String getTradeSnapshot() { return tradeSnapshot; }
    public void setTradeSnapshot(String tradeSnapshot) { this.tradeSnapshot = tradeSnapshot; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getChangeDiff() { return changeDiff; }
    public void setChangeDiff(String changeDiff) { this.changeDiff = changeDiff; }

    public String getAmendedBy() { return amendedBy; }
    public void setAmendedBy(String amendedBy) { this.amendedBy = amendedBy; }

    public LocalDateTime getAmendedAt() { return amendedAt; }
    public void setAmendedAt(LocalDateTime amendedAt) { this.amendedAt = amendedAt; }

    public String getAmendmentReason() { return amendmentReason; }
    public void setAmendmentReason(String amendmentReason) { this.amendmentReason = amendmentReason; }

    public Long getApprovalId() { return approvalId; }
    public void setApprovalId(Long approvalId) { this.approvalId = approvalId; }
}
