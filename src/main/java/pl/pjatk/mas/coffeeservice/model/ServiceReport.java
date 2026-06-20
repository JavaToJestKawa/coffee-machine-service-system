package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class ServiceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data raportu jest wymagana.")
    @Column(nullable = false)
    private LocalDateTime reportDate;

    @NotBlank(message = "Podsumowanie prac jest wymagane.")
    @Column(nullable = false)
    private String workSummary;

    @NotBlank(message = "Zalecenia są wymagane.")
    @Column(nullable = false)
    private String recommendations;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, unique = true)
    private ServiceRequest serviceRequest;

    protected ServiceReport() {
    }

    public ServiceReport(LocalDateTime reportDate,
                         String workSummary,
                         String recommendations) {
        this.reportDate = requireNonNull(reportDate, "Data raportu jest wymagana.");
        this.workSummary = requireText(workSummary, "Podsumowanie prac jest wymagane.");
        this.recommendations = requireText(recommendations, "Zalecenia są wymagane.");
    }

    /**
     * Oblicza końcowy koszt serwisu jako sumę kosztów wszystkich napraw przypisanych do zgłoszenia.
     *
     * @return końcowy koszt serwisu
     */
    public BigDecimal calculateFinalCost() {
        if (serviceRequest == null) {
            return BigDecimal.ZERO;
        }

        return serviceRequest.getRepairs()
                .stream()
                .map(Repair::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void assignServiceRequest(ServiceRequest serviceRequest) {
        if (serviceRequest == null) {
            throw new IllegalArgumentException("Zgłoszenie jest wymagane.");
        }

        if (this.serviceRequest == serviceRequest) {
            return;
        }

        if (this.serviceRequest != null) {
            this.serviceRequest.removeServiceReport();
        }

        this.serviceRequest = serviceRequest;

        if (serviceRequest.getServiceReport() != this) {
            serviceRequest.attachServiceReport(this);
        }
    }

    public void removeServiceRequest() {
        if (this.serviceRequest != null) {
            ServiceRequest oldRequest = this.serviceRequest;
            this.serviceRequest = null;

            if (oldRequest.getServiceReport() == this) {
                oldRequest.removeServiceReport();
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

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public String getWorkSummary() {
        return workSummary;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
}