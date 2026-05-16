package com.telcoapihub.billing;

import com.telcoapihub.common.ApiException;
import com.telcoapihub.domain.ApiProduct;
import com.telcoapihub.domain.Enterprise;
import com.telcoapihub.domain.Invoice;
import com.telcoapihub.domain.InvoiceLineItem;
import com.telcoapihub.domain.PricingPlan;
import com.telcoapihub.domain.UsageRecord;
import com.telcoapihub.repository.EnterpriseRepository;
import com.telcoapihub.repository.InvoiceRepository;
import com.telcoapihub.repository.UsageRecordRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Rolls un-invoiced usage for a billing period into an invoice: an optional
 * recurring platform fee plus one tier-rated line per product. Invoiced
 * usage records are stamped so they are never billed twice.
 */
@Service
public class InvoicingService {

    private final UsageRecordRepository usageRecordRepository;
    private final InvoiceRepository invoiceRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final RatingService ratingService;

    public InvoicingService(UsageRecordRepository usageRecordRepository,
                            InvoiceRepository invoiceRepository,
                            EnterpriseRepository enterpriseRepository,
                            RatingService ratingService) {
        this.usageRecordRepository = usageRecordRepository;
        this.invoiceRepository = invoiceRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.ratingService = ratingService;
    }

    @Transactional
    public Invoice generate(Long enterpriseId, LocalDate periodStart, LocalDate periodEnd) {
        if (periodEnd.isBefore(periodStart)) {
            throw ApiException.badRequest("periodEnd must not be before periodStart");
        }
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> ApiException.notFound("Enterprise not found"));
        PricingPlan plan = enterprise.getPricingPlan();
        if (plan == null) {
            throw ApiException.badRequest("Enterprise has no pricing plan assigned");
        }

        Instant from = periodStart.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = periodEnd.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        List<UsageRecord> records = usageRecordRepository
                .findByEnterpriseIdAndSuccessTrueAndInvoiceIdIsNullAndOccurredAtBetween(
                        enterpriseId, from, to);

        Map<ApiProduct, Long> volumeByProduct = records.stream()
                .collect(Collectors.groupingBy(UsageRecord::getProduct, Collectors.counting()));

        Invoice invoice = new Invoice();
        invoice.setEnterpriseId(enterpriseId);
        invoice.setCurrency(plan.getCurrency());
        invoice.setPeriodStart(periodStart);
        invoice.setPeriodEnd(periodEnd);

        BigDecimal total = BigDecimal.ZERO;
        if (plan.getMonthlyFee() != null && plan.getMonthlyFee().signum() > 0) {
            BigDecimal fee = plan.getMonthlyFee().setScale(2, RoundingMode.HALF_UP);
            invoice.getLineItems().add(
                    new InvoiceLineItem("Platform subscription", null, 1, fee));
            total = total.add(fee);
        }
        for (ApiProduct product : ApiProduct.values()) {
            long quantity = volumeByProduct.getOrDefault(product, 0L);
            if (quantity == 0) {
                continue;
            }
            BigDecimal amount = ratingService.rate(plan, product, quantity)
                    .setScale(2, RoundingMode.HALF_UP);
            invoice.getLineItems().add(
                    new InvoiceLineItem(product.label() + " queries", product, quantity, amount));
            total = total.add(amount);
        }
        invoice.setTotal(total.setScale(2, RoundingMode.HALF_UP));

        Invoice saved = invoiceRepository.save(invoice);
        records.forEach(record -> record.setInvoiceId(saved.getId()));
        usageRecordRepository.saveAll(records);
        return saved;
    }
}
