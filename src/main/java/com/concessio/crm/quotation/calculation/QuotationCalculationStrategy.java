package com.concessio.crm.quotation.calculation;

import com.concessio.crm.quotation.model.Quotation;

/**
 * Strategy interface for quotation calculations.
 * Implementations handle specific calculation logic for different quotation types.
 */
public interface QuotationCalculationStrategy {

    /**
     * Validates the quotation data before calculation.
     *
     * @param quotation the quotation to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validate(Quotation quotation);

    /**
     * Performs calculations specific to this quotation type.
     *
     * @param quotation the quotation to calculate
     */
    void calculate(Quotation quotation);

    /**
     * Returns the type of quotation this strategy handles.
     *
     * @return the quotation type
     */
    com.concessio.crm.quotation.model.QuotationType getType();
}
