package com.telcoapihub.billing;

import com.telcoapihub.common.ApiException;
import com.telcoapihub.domain.Invoice;
import com.telcoapihub.domain.UsageRecord;
import com.telcoapihub.repository.InvoiceRepository;
import com.telcoapihub.repository.UsageRecordRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Operations endpoints for usage inspection and invoice generation.
 * Requires the admin API key (ROLE_ADMIN).
 */
@RestController
@RequestMapping("/admin")
public class BillingController {

    private final InvoicingService invoicingService;
    private final InvoiceRepository invoiceRepository;
    private final UsageRecordRepository usageRecordRepository;

    public BillingController(InvoicingService invoicingService,
                             InvoiceRepository invoiceRepository,
                             UsageRecordRepository usageRecordRepository) {
        this.invoicingService = invoicingService;
        this.invoiceRepository = invoiceRepository;
        this.usageRecordRepository = usageRecordRepository;
    }

    @GetMapping("/usage")
    public List<UsageRecord> usage(
            @RequestParam Long enterpriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return usageRecordRepository.findByEnterpriseIdAndOccurredAtBetween(
                enterpriseId,
                from.atStartOfDay(ZoneOffset.UTC).toInstant(),
                to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
    }

    @PostMapping("/invoices")
    public Invoice generateInvoice(
            @RequestParam Long enterpriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        return invoicingService.generate(enterpriseId, periodStart, periodEnd);
    }

    @GetMapping("/invoices")
    public List<Invoice> listInvoices(@RequestParam Long enterpriseId) {
        return invoiceRepository.findByEnterpriseIdOrderByGeneratedAtDesc(enterpriseId);
    }

    @GetMapping("/invoices/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Invoice not found"));
    }
}
