package com.trading.ctrm.trade.dto;

import com.trading.ctrm.trade.EnumType.BuySell;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Request DTO for booking a trade from a template with optional valuation config
 */
public class BookFromTemplateRequest {
    
    private Long templateId;
    private BigDecimal quantity;
    private BuySell buySell;
    private String counterparty;
    private String portfolio;
    private String createdByUser;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tradeDate;
    private ValuationConfigRequest valuationConfig;

    // Getters and Setters
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BuySell getBuySell() { return buySell; }
    public void setBuySell(BuySell buySell) { this.buySell = buySell; }

    public String getCounterparty() { return counterparty; }
    public void setCounterparty(String counterparty) { this.counterparty = counterparty; }

    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

    public String getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(String createdByUser) { this.createdByUser = createdByUser; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public ValuationConfigRequest getValuationConfig() { return valuationConfig; }
    public void setValuationConfig(ValuationConfigRequest valuationConfig) { 
        this.valuationConfig = valuationConfig; 
    }
}
