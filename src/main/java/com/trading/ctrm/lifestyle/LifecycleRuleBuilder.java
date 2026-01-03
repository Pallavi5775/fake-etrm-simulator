package com.trading.ctrm.lifestyle;

import com.trading.ctrm.lifestyle.dto.LifecycleRuleRequest;
import org.springframework.lang.NonNull;



public class LifecycleRuleBuilder {

    @NonNull
    public static LifecycleRule fromRequest(@NonNull LifecycleRuleRequest req) {

        LifecycleRule rule = new LifecycleRule(); // ✅ JPA-safe

        // 🔑 Core lifecycle matching
        rule.setFromStatus(req.getFromStatus());
        rule.setEventType(req.getEventType());
        rule.setToStatus(req.getToStatus());
        rule.setDesk(req.getDesk());

        // Ensure event is always set (for DB NOT NULL constraint)
        if (req.getEventType() != null) {
            rule.setEvent(req.getEventType().name());
        } else {
            rule.setEvent(null);
        }

        // 🔁 Rule execution constraints
        rule.setMaxOccurrence(
            req.getMaxOccurrence() != 0 ? req.getMaxOccurrence() : 1
        );

        // 🧪 Simulator visibility
        rule.setEnabled(req.isEnabled());

        // 🚫 NEVER auto-promote to production
        rule.setProductionEnabled(false);

        // 📅 Validity window (optional but safe)
        rule.setEffectiveFrom(req.getEffectiveFrom());
        rule.setEffectiveTo(req.getEffectiveTo());

        // 🏷 Optional display label (NOT identity)
        if (req.getName() != null && !req.getName().isBlank()) {
            rule.setName(req.getName());
        }

        return rule;
    }
}

