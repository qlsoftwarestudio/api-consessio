package com.concessio.crm.quotation.calculation;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Strategy for FIAT PLAN (Plan Fiat) quotations.
 * Supports different plan types: 100%, 70/30, 50/50, ADQUIRIDO.
 */
@Component
public class PlanFiatCalculationStrategy implements QuotationCalculationStrategy {

    private static final List<String> VALID_PLAN_TYPES = List.of("100%", "70/30", "50/50", "ADQUIRIDO");
    private static final BigDecimal ADJUDICATION_PERCENTAGE = new BigDecimal("0.30"); // 30% adjudicación

    @Override
    public void validate(Quotation quotation) {
        if (quotation.getPriceFinal() == null) {
            throw new IllegalArgumentException("El precio final es obligatorio");
        }
        if (quotation.getPlanType() == null) {
            throw new IllegalArgumentException("El tipo de plan Fiat es obligatorio");
        }
        if (!VALID_PLAN_TYPES.contains(quotation.getPlanType())) {
            throw new IllegalArgumentException("Tipo de plan inválido. Opciones: 100%, 70/30, 50/50, ADQUIRIDO");
        }

        // Set default installments if not provided
        if (quotation.getPlanInstallments() == null) {
            quotation.setPlanInstallments(84); // Default 84 months
        }

        // Validate installments range
        if (quotation.getPlanInstallments() < 12 || quotation.getPlanInstallments() > 120) {
            throw new IllegalArgumentException("Las cuotas del plan deben estar entre 12 y 120");
        }
    }

    @Override
    public void calculate(Quotation quotation) {
        BigDecimal finalPrice = quotation.getPriceFinal();
        String planType = quotation.getPlanType();
        int installments = quotation.getPlanInstallments();

        BigDecimal initialPayment;
        BigDecimal installmentAmount;

        switch (planType) {
            case "100%":
                // 100% financing - no initial payment
                initialPayment = BigDecimal.ZERO;
                installmentAmount = calculateInstallmentAmount(finalPrice, installments, BigDecimal.ZERO);
                break;

            case "70/30":
                // 30% initial payment, 70% in installments
                initialPayment = finalPrice.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
                BigDecimal remaining70 = finalPrice.subtract(initialPayment);
                installmentAmount = calculateInstallmentAmount(remaining70, installments, BigDecimal.ZERO);
                break;

            case "50/50":
                // 50% initial payment, 50% in installments
                initialPayment = finalPrice.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);
                BigDecimal remaining50 = finalPrice.subtract(initialPayment);
                installmentAmount = calculateInstallmentAmount(remaining50, installments, BigDecimal.ZERO);
                break;

            case "ADQUIRIDO":
                // Pre-owned vehicle plan
                // Lower initial payment, rest in installments
                initialPayment = finalPrice.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
                BigDecimal remainingAdquirido = finalPrice.subtract(initialPayment);
                // Slightly higher rate for used vehicles
                installmentAmount = calculateInstallmentAmount(remainingAdquirido, installments, new BigDecimal("0.015"));
                break;

            default:
                throw new IllegalArgumentException("Tipo de plan no soportado: " + planType);
        }

        quotation.setDownPayment(initialPayment);
        quotation.setPlanInstallmentAmount(installmentAmount);

        // Calculate total cost
        BigDecimal totalInstallments = installmentAmount.multiply(BigDecimal.valueOf(installments));
        BigDecimal totalCost = initialPayment.add(totalInstallments);

        quotation.setTotalFinancingCost(totalCost);
        quotation.setTotalInterest(totalCost.subtract(finalPrice));

        // Set plan adjudication month (cuota en que se adjudica)
        quotation.setPlanAdjudication(calculateAdjudicationMonth(planType));
    }

    /**
     * Calculate the adjudication month based on plan type.
     */
    private int calculateAdjudicationMonth(String planType) {
        return switch (planType) {
            case "70/30" -> 5;      // Adjudicación típica mes 5
            case "50/50" -> 4;      // Adjudicación típica mes 4
            case "ADQUIRIDO" -> 1;  // Casi inmediato para usados
            case "100%" -> 6;       // Adjudicación promedio mes 6
            default -> 6;           // Default: mes 6
        };
    }

    /**
     * Calculate installment amount with optional interest rate.
     * Simple calculation: (capital / months) + interest
     */
    private BigDecimal calculateInstallmentAmount(BigDecimal capital, int months, BigDecimal monthlyRate) {
        BigDecimal baseInstallment = capital.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        if (monthlyRate != null && monthlyRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal interest = capital.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            return baseInstallment.add(interest);
        }

        return baseInstallment;
    }

    @Override
    public QuotationType getType() {
        return QuotationType.PLAN_FIAT;
    }
}
