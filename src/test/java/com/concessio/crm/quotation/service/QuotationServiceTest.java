package com.concessio.crm.quotation.service;

import com.concessio.crm.activity.repository.ActivityRepository;
import com.concessio.crm.quotation.calculation.QuotationCalculationContext;
import com.concessio.crm.quotation.calculation.ContadoCalculationStrategy;
import com.concessio.crm.quotation.calculation.FinanciadoCalculationStrategy;
import com.concessio.crm.quotation.calculation.PlanFiatCalculationStrategy;
import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import com.concessio.crm.quotation.repository.QuotationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class QuotationServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private ActivityRepository activityRepository;

    private QuotationService quotationService;
    private QuotationCalculationContext calculationContext;

    private Quotation quotation;

    @BeforeEach
    void setUp() {
        // Initialize strategies
        ContadoCalculationStrategy contadoStrategy = new ContadoCalculationStrategy();
        FinanciadoCalculationStrategy financiadoStrategy = new FinanciadoCalculationStrategy();
        PlanFiatCalculationStrategy planFiatStrategy = new PlanFiatCalculationStrategy();

        // Initialize context with all strategies
        calculationContext = new QuotationCalculationContext(
                List.of(contadoStrategy, financiadoStrategy, planFiatStrategy)
        );

        // Initialize service with context
        quotationService = new QuotationService(quotationRepository, activityRepository, calculationContext);

        quotation = new Quotation();
        quotation.setId(1L);
        quotation.setPriceList(new BigDecimal("18500000"));
        quotation.setDiscount(new BigDecimal("500000"));
        quotation.setPriceFinal(new BigDecimal("18000000"));

        // Setup lead mock to avoid NPE in activity logging
        com.concessio.crm.lead.model.Lead lead = new com.concessio.crm.lead.model.Lead();
        lead.setId(1L);
        lead.setFirstName("Juan");
        quotation.setLead(lead);

        lenient().when(activityRepository.save(any())).thenReturn(null);
    }

    @Test
    void testCalculateContado_Success() {
        quotation.setType(QuotationType.CONTADO);

        when(quotationRepository.save(any())).thenReturn(quotation);

        Quotation result = quotationService.createQuotation(quotation, 1L, 1L);

        assertNotNull(result);
        assertEquals(QuotationType.CONTADO, result.getType());
        assertEquals(new BigDecimal("18000000"), result.getPriceFinal());
    }

    @Test
    void testCalculateFinancing_MonthlyPayment() {
        quotation.setType(QuotationType.FINANCIADO);
        quotation.setDownPayment(new BigDecimal("5400000")); // 30%
        quotation.setFinancingMonths(48);
        quotation.setInterestRate(new BigDecimal("25.5"));
        quotation.setBank("Banco Nación");

        when(quotationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Quotation result = quotationService.createQuotation(quotation, 1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getMonthlyPayment());
        assertTrue(result.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);

        // Verificar que el monto financiado es correcto: 18M - 5.4M = 12.6M
        BigDecimal expectedFinanced = new BigDecimal("12600000");
        BigDecimal actualFinanced = result.getPriceFinal().subtract(result.getDownPayment());
        assertEquals(expectedFinanced, actualFinanced);
    }

    @Test
    void testCalculatePlanFiat_Payment() {
        quotation.setType(QuotationType.PLAN_FIAT);
        quotation.setPlanType("70/30");
        quotation.setPlanInstallments(84);

        when(quotationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Quotation result = quotationService.createQuotation(quotation, 1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getPlanInstallmentAmount());
        assertNotNull(result.getPlanAdjudication());
        assertEquals(5, result.getPlanAdjudication()); // Mes 5 para plan 70/30
    }

    @Test
    void testInvalidFinancing_ThrowsException() {
        quotation.setType(QuotationType.FINANCIADO);
        quotation.setDownPayment(null); // Falta entrega

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> quotationService.createQuotation(quotation, 1L, 1L)
        );

        assertTrue(exception.getMessage().contains("entrega"));
    }

    @Test
    void testCalculateFinalPrice_AutoCalculate() {
        quotation.setPriceFinal(null); // Sin precio final
        quotation.setType(QuotationType.CONTADO);

        when(quotationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Quotation result = quotationService.createQuotation(quotation, 1L, 1L);

        assertEquals(new BigDecimal("18000000"), result.getPriceFinal());
    }
}
