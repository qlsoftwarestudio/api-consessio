package com.concessio.crm.quotation.calculation;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy for CASH (Contado) quotations.
 * Simple validation - just needs a final price.
 */
@Component
public class ContadoCalculationStrategy implements QuotationCalculationStrategy {

    @Override
    public void validate(Quotation quotation) {
        if (quotation.getPriceFinal() == null) {
            throw new IllegalArgumentException("El precio final es obligatorio para cotización al contado");
        }
        if (quotation.getPriceFinal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio final debe ser mayor a cero");
        }
    }

    @Override
    public void calculate(Quotation quotation) {
        // For cash payments, no additional calculations needed
        // The final price is already set
        quotation.setMonthlyPayment(null);
        quotation.setTotalInterest(BigDecimal.ZERO);
        quotation.setTotalFinancingCost(quotation.getPriceFinal());
    }

    @Override
    public QuotationType getType() {
        return QuotationType.CONTADO;
    }
}
