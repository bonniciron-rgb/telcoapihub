package com.telcoapihub.repository;

import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.ConsentRecord;
import com.telcoapihub.domain.ConsentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRepository extends JpaRepository<ConsentRecord, Long> {

    List<ConsentRecord> findByEnterpriseIdAndMsisdnAndProductAndStatus(
            Long enterpriseId, String msisdn, ApiProduct product, ConsentStatus status);

    List<ConsentRecord> findByEnterpriseId(Long enterpriseId);
}
