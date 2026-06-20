package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data diagnozy jest wymagana.")
    @Column(nullable = false)
    private LocalDateTime diagnosisDate;

    @NotBlank(message = "Opis usterki jest wymagany.")
    @Column(nullable = false)
    private String faultDescription;

    @DecimalMin(value = "0.00", message = "Koszt szacowany nie może być ujemny.")
    private BigDecimal estimatedCost;

    @Column(nullable = false)
    private boolean repairPossible;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, unique = true)
    private ServiceRequest serviceRequest;

    protected Diagnosis() {
    }

    public Diagnosis(LocalDateTime diagnosisDate,
                     String faultDescription,
                     BigDecimal estimatedCost,
                     boolean repairPossible) {
        this.diagnosisDate = requireNonNull(diagnosisDate, "Data diagnozy jest wymagana.");
        this.faultDescription = requireText(faultDescription, "Opis usterki jest wymagany.");
        this.repairPossible = repairPossible;

        if (!repairPossible && estimatedCost != null) {
            throw new IllegalArgumentException("Koszt szacowany powinien być pusty, jeżeli naprawa nie jest możliwa.");
        }

        if (estimatedCost != null && estimatedCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Koszt szacowany nie może być ujemny.");
        }

        this.estimatedCost = estimatedCost;
    }

    public void assignServiceRequest(ServiceRequest serviceRequest) {
        if (serviceRequest == null) {
            throw new IllegalArgumentException("Zgłoszenie jest wymagane.");
        }

        if (this.serviceRequest == serviceRequest) {
            return;
        }

        if (this.serviceRequest != null) {
            this.serviceRequest.removeDiagnosis();
        }

        this.serviceRequest = serviceRequest;

        if (serviceRequest.getDiagnosis() != this) {
            serviceRequest.attachDiagnosis(this);
        }
    }

    public void removeServiceRequest() {
        if (this.serviceRequest != null) {
            ServiceRequest oldRequest = this.serviceRequest;
            this.serviceRequest = null;

            if (oldRequest.getDiagnosis() == this) {
                oldRequest.removeDiagnosis();
            }
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }

    private <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDiagnosisDate() {
        return diagnosisDate;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public boolean isRepairPossible() {
        return repairPossible;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
}