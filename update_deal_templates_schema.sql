-- Add commodity_id column to deal_templates table
-- This supports the @ManyToOne relationship to Commodity entity

ALTER TABLE ctrm.deal_templates ADD COLUMN commodity_id BIGINT;
ALTER TABLE ctrm.deal_templates ADD CONSTRAINT fk_deal_templates_commodity 
    FOREIGN KEY (commodity_id) REFERENCES ctrm.commodity(id);

-- Create index for better performance
CREATE INDEX idx_deal_templates_commodity ON ctrm.deal_templates(commodity_id);