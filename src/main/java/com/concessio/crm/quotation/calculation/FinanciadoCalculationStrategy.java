package com.concessio.crm.quotation.calculation;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Strategy for FINANCED (Financiado) quotations.
 * Uses French amortization system (fixed installment).
 * Formula: C = P * (i * (1+i)^n) / ((1+i)^n - 1)
 */
@Component
public class FinanciadoCalculationStrategy implements QuotationCalculationStrategy {

    @Override
    public void validate(Quotation quotation) {
        if (quotation.getPriceFinal() == null) {
            throw new IllegalArgumentException("El precio final es obligatorio");
        }
        if (quotation.getDownPayment() == null) {
            throw new IllegalArgumentException("La entrega mínima es obligatoria para financiación");
        }
        if (quotation.getFinancingMonths() == null) {
            throw new IllegalArgumentException("La cantidad de cuotas es obligatoria");
        }
        if (quotation.getInterestRate() == null) {
            throw new IllegalArgumentException("La tasa de interés es obligatoria");
        }

        // Validate that down payment is not greater than final price
        if (quotation.getDownPayment().compareTo(quotation.getPriceFinal()) > 0) {
            throw new IllegalArgumentException("La entrega no puede ser mayor al precio final");
        }

        // Validate months range
        if (quotation.getFinancingMonths() < 1 || quotation.getFinancingMonths() > 120) {
            throw new IllegalArgumentException("La cantidad de cuotas debe estar entre 1 y 120");
        }

        // Validate interest rate
        if (quotation.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La tasa de interés no puede ser negativa");
        }
    }

    @Override
    public void calculate(Quotation quotation) {
        BigDecimal capital = quotation.getPriceFinal().subtract(quotation.getDownPayment());
        BigDecimal monthlyRate = quotation.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        int months = quotation.getFinancingMonths();

        // If interest rate is 0, simple division
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal monthlyPayment = capital.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
            quotation.setMonthlyPayment(monthlyPayment);
            quotation.setTotalFinancingCost(capital);
            quotation.setTotalInterest(BigDecimal.ZERO);
            return;
        }

        // (1 + i)^n
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(months, MathContext.DECIMAL128);

        // Monthly Payment = P * (i * factor) / (factor - 1)
        BigDecimal monthlyPayment = capital
                .multiply(monthlyRate.multiply(factor))
                .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        quotation.setMonthlyPayment(monthlyPayment);

        // Calculate totals
        BigDecimal totalPaid = monthlyPayment.multiply(BigDecimal.valueOf(months));
        quotation.setTotalFinancingCost(totalPaid);
        quotation.setTotalInterest(totalPaid.subtract(capital));
    }

    @Override
    public QuotationType getType() {
        return QuotationType.FINANCIADO;
    }
}
