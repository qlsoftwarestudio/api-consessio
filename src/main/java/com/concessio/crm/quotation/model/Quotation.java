package com.concessio.crm.quotation.model;

import com.concessio.crm.lead.model.Lead;
import com.concessio.crm.tenant.model.Tenant;
import com.concessio.crm.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead; // Lead asociado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Vendedor que cotizó

    @NotNull
    @Enumerated(EnumType.STRING)
    private QuotationType type; // CONTADO, FINANCIADO, PLAN_FIAT

    // Datos del vehículo cotizado
    private String vehicleModel;
    private String vehicleVin;

    // Precios base
    @NotNull
    private BigDecimal priceList; // Precio de lista
    private BigDecimal discount; // Descuento aplicado
    private BigDecimal priceFinal; // Precio final (lista - descuento)

    // Para FINANCIADO
    private BigDecimal downPayment; // Entrega mínima
    private Integer financingMonths; // Cuotas (24, 36, 48, 60)
    private BigDecimal interestRate; // Tasa anual
    private BigDecimal monthlyPayment; // Cuota mensual calculada
    private String bank; // Banco financista

    // Para PLAN FIAT
    private String planType; // "100%", "70/30", "50/50", "ADQUIRIDO"
    private Integer planInstallments; // Cantidad de cuotas
    private BigDecimal planInstallmentAmount; // Monto cuota
    private Integer planAdjudication; // Cuota de adjudicación

    @Column(length = 500)
    private String notes; // Notas adicionales

    private LocalDateTime createdAt;
    private LocalDateTime validUntil; // Validez de la cotización

    private boolean sentToCustomer; // Si fue enviada al cliente
    private LocalDateTime sentAt;

    // Campos calculados para financiamiento
    private BigDecimal totalFinancingCost;  // Costo total del financiamiento
    private BigDecimal totalInterest;       // Total de intereses pagados

    // Constructor
    public Quotation() {
        this.createdAt = LocalDateTime.now();
        this.validUntil = LocalDateTime.now().plusDays(7); // 7 días de validez
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public QuotationType getType() { return type; }
    public void setType(QuotationType type) { this.type = type; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleVin() { return vehicleVin; }
    public void setVehicleVin(String vehicleVin) { this.vehicleVin = vehicleVin; }

    public BigDecimal getPriceList() { return priceList; }
    public void setPriceList(BigDecimal priceList) { this.priceList = priceList; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getPriceFinal() { return priceFinal; }
    public void setPriceFinal(BigDecimal priceFinal) { this.priceFinal = priceFinal; }

    public BigDecimal getDownPayment() { return downPayment; }
    public void setDownPayment(BigDecimal downPayment) { this.downPayment = downPayment; }

    public Integer getFinancingMonths() { return financingMonths; }
    public void setFinancingMonths(Integer financingMonths) { this.financingMonths = financingMonths; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }

    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public Integer getPlanInstallments() { return planInstallments; }
    public void setPlanInstallments(Integer planInstallments) { this.planInstallments = planInstallments; }

    public BigDecimal getPlanInstallmentAmount() { return planInstallmentAmount; }
    public void setPlanInstallmentAmount(BigDecimal planInstallmentAmount) { this.planInstallmentAmount = planInstallmentAmount; }

    public Integer getPlanAdjudication() { return planAdjudication; }
    public void setPlanAdjudication(Integer planAdjudication) { this.planAdjudication = planAdjudication; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }

    public boolean isSentToCustomer() { return sentToCustomer; }
    public void setSentToCustomer(boolean sentToCustomer) { this.sentToCustomer = sentToCustomer; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public BigDecimal getTotalFinancingCost() { return totalFinancingCost; }
    public void setTotalFinancingCost(BigDecimal totalFinancingCost) { this.totalFinancingCost = totalFinancingCost; }

    public BigDecimal getTotalInterest() { return totalInterest; }
    public void setTotalInterest(BigDecimal totalInterest) { this.totalInterest = totalInterest; }

    // Método de utilidad para calcular precio final
    public void calculateFinalPrice() {
        if (priceList != null && discount != null) {
            this.priceFinal = priceList.subtract(discount);
        }
    }

    // Método de utilidad para calcular cuota financiada (simplificado)
    public void calculateMonthlyPayment() {
        if (priceFinal != null && downPayment != null && financingMonths != null && interestRate != null) {
            BigDecimal amountToFinance = priceFinal.subtract(downPayment);
            // Fórmula simplificada - en producción usar fórmula de amortización real
            BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), 10, BigDecimal.ROUND_HALF_UP);
            // Cálculo básico para MVP
            this.monthlyPayment = amountToFinance.divide(BigDecimal.valueOf(financingMonths), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
