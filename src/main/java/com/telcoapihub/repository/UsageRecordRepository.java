package com.telcoapihub.repository;

import com.telcoapihub.domain.UsageRecord;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

    List<UsageRecord> findByEnterpriseIdAndOccurredAtBetween(Long enterpriseId, Instant from, Instant to);

    List<UsageRecord> findByEnterpriseIdAndSuccessTrueAndInvoiceIdIsNullAndOccurredAtBetween(
            Long enterpriseId, Instant from, Instant to);
}
