package com.trading.ctrm.deals;

import java.math.BigDecimal;

public class DealTemplateDto {
    
    private Long id;
    private String templateName;
    private String commodity;
    private String instrumentType;
    private Long instrumentId;
    private String instrumentCode;
    private String pricingModel;
    private boolean autoApprovalAllowed;
    private BigDecimal defaultQuantity;
    private BigDecimal defaultPrice;
    private String unit;
    private String currency;
    private BigDecimal mtmApprovalThreshold;

    // Static factory method
    public static DealTemplateDto from(DealTemplate template) {
        DealTemplateDto dto = new DealTemplateDto();
        dto.setId(template.getId());
        dto.setTemplateName(template.getTemplateName());
        
        // Populate from instrument if template fields are null
        if (template.getInstrument() != null) {
            dto.setInstrumentId(template.getInstrument().getId());
            dto.setInstrumentCode(template.getInstrument().getInstrumentCode());
            
            // Use template values if set, otherwise fall back to instrument
            dto.setCommodity(template.getCommodity() != null ? 
                template.getCommodity() : template.getInstrument().getCommodity());
            dto.setInstrumentType(template.getInstrumentType() != null ? 
                template.getInstrumentType() : template.getInstrument().getInstrumentType().name());
            dto.setUnit(template.getUnit() != null ? 
                template.getUnit() : template.getInstrument().getUnit());
            dto.setCurrency(template.getCurrency() != null ? 
                template.getCurrency() : template.getInstrument().getCurrency());
            
            // Set pricing model based on instrument type if not set
            if (template.getPricingModel() != null) {
                dto.setPricingModel(template.getPricingModel());
            } else {
                String instrumentType = template.getInstrument().getInstrumentType().name();
                String pricingModel = switch (instrumentType) {
                    case "POWER_FORWARD" -> "POWER_FORWARD";
                    case "OPTION" -> "Black76";
                    case "RENEWABLE_PPA" -> "RENEWABLE_FORECAST";
                    case "GAS_FORWARD", "COMMODITY_SWAP", "FREIGHT" -> "DCF";
                    default -> "DCF";
                };
                dto.setPricingModel(pricingModel);
            }
        } else {
            // No instrument - use template values directly (may be null)
            dto.setCommodity(template.getCommodity());
            dto.setInstrumentType(template.getInstrumentType());
            dto.setUnit(template.getUnit());
            dto.setCurrency(template.getCurrency());
            dto.setPricingModel(template.getPricingModel());
        }
        
        dto.setAutoApprovalAllowed(template.isAutoApprovalAllowed());
        dto.setDefaultQuantity(template.getDefaultQuantity());
        dto.setDefaultPrice(template.getDefaultPrice());
        dto.setMtmApprovalThreshold(template.getMtmApprovalThreshold());
        
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public boolean isAutoApprovalAllowed() {
        return autoApprovalAllowed;
    }

    public void setAutoApprovalAllowed(boolean autoApprovalAllowed) {
        this.autoApprovalAllowed = autoApprovalAllowed;
    }

    public BigDecimal getDefaultQuantity() {
        return defaultQuantity;
    }

    public void setDefaultQuantity(BigDecimal defaultQuantity) {
        this.defaultQuantity = defaultQuantity;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMtmApprovalThreshold() {
        return mtmApprovalThreshold;
    }

    public void setMtmApprovalThreshold(BigDecimal mtmApprovalThreshold) {
        this.mtmApprovalThreshold = mtmApprovalThreshold;
    }
}
