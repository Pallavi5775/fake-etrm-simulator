package com.trading.ctrm.rules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {
    
    List<ApprovalTask> findByTradeId(String tradeId);
    
    List<ApprovalTask> findByStatus(String status);
    
    Optional<ApprovalTask> findByTradeIdAndStatus(String tradeId, String status);
}
