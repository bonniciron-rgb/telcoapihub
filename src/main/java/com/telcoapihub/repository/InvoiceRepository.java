package com.telcoapihub.repository;

import com.telcoapihub.domain.Invoice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByEnterpriseIdOrderByGeneratedAtDesc(Long enterpriseId);
}
