package com.concessio.crm.quotation.calculation;

import com.concessio.crm.quotation.model.Quotation;
import com.concessio.crm.quotation.model.QuotationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Context class for managing quotation calculation strategies.
 * Uses Strategy Pattern to select appropriate calculation logic based on quotation type.
 */
@Component
public class QuotationCalculationContext {

    private final Map<QuotationType, QuotationCalculationStrategy> strategies;

    /**
     * Constructor that injects all available strategies.
     * Spring automatically collects all beans implementing QuotationCalculationStrategy.
     */
    public QuotationCalculationContext(List<QuotationCalculationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        QuotationCalculationStrategy::getType,
                        Function.identity()
                ));
    }

    /**
     * Validates a quotation based on its type.
     *
     * @param quotation the quotation to validate
     * @throws IllegalArgumentException if no strategy found or validation fails
     */
    public void validate(Quotation quotation) {
        QuotationCalculationStrategy strategy = getStrategy(quotation.getType());
        strategy.validate(quotation);
    }

    /**
     * Calculates quotation values based on its type.
     *
     * @param quotation the quotation to calculate
     * @throws IllegalArgumentException if no strategy found
     */
    public void calculate(Quotation quotation) {
        QuotationCalculationStrategy strategy = getStrategy(quotation.getType());
        strategy.calculate(quotation);
    }

    /**
     * Gets the appropriate strategy for a quotation type.
     *
     * @param type the quotation type
     * @return the calculation strategy
     * @throws IllegalArgumentException if no strategy found for the type
     */
    public QuotationCalculationStrategy getStrategy(QuotationType type) {
        QuotationCalculationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException(
                "No calculation strategy found for quotation type: " + type +
                ". Available types: " + strategies.keySet()
            );
        }
        return strategy;
    }

    /**
     * Checks if a strategy exists for the given quotation type.
     *
     * @param type the quotation type
     * @return true if a strategy exists
     */
    public boolean hasStrategy(QuotationType type) {
        return strategies.containsKey(type);
    }

    /**
     * Returns all available quotation types.
     *
     * @return list of supported types
     */
    public List<QuotationType> getSupportedTypes() {
        return List.copyOf(strategies.keySet());
    }
}
