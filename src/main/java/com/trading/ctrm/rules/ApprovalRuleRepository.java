package com.trading.ctrm.rules;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRuleRepository extends JpaRepository<ApprovalRule, Long> {


    @Query("""
  SELECT r FROM ApprovalRule r
  WHERE r.active = true
    AND r.triggerEvent = :event
  ORDER BY r.priority ASC
""")
List<ApprovalRule> findActiveRules(String event);


@Modifying
@Query("""
 UPDATE ApprovalRule r
 SET r.active = false,
     r.status = 'RETIRED'
 WHERE r.parentRuleId = :parent
""")
void deactivateAllVersions(Long parent);
}
