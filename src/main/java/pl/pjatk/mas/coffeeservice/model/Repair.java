package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pjatk.mas.coffeeservice.model.enums.RepairResult;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data rozpoczęcia naprawy jest wymagana.")
    @Column(nullable = false)
    private LocalDateTime repairStartDate;

    @NotNull(message = "Data zakończenia naprawy jest wymagana.")
    @Column(nullable = false)
    private LocalDateTime repairFinishDate;

    @NotNull(message = "Wynik naprawy jest wymagany.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairResult result;

    @NotBlank(message = "Opis naprawy jest wymagany.")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Koszt naprawy jest wymagany.")
    @DecimalMin(value = "0.00", message = "Koszt naprawy nie może być ujemny.")
    @Column(nullable = false)
    private BigDecimal cost;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ServiceRequest serviceRequest;

    protected Repair() {
    }

    public Repair(LocalDateTime repairStartDate,
                  LocalDateTime repairFinishDate,
                  RepairResult result,
                  String description,
                  BigDecimal cost) {
        this.repairStartDate = requireNonNull(repairStartDate, "Data rozpoczęcia naprawy jest wymagana.");
        this.repairFinishDate = requireNonNull(repairFinishDate, "Data zakończenia naprawy jest wymagana.");

        if (repairFinishDate.isBefore(repairStartDate)) {
            throw new IllegalArgumentException("Data zakończenia naprawy nie może być wcześniejsza niż data rozpoczęcia.");
        }

        this.result = requireNonNull(result, "Wynik naprawy jest wymagany.");
        this.description = requireText(description, "Opis naprawy jest wymagany.");
        this.cost = requireNonNull(cost, "Koszt naprawy jest wymagany.");

        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Koszt naprawy nie może być ujemny.");
        }
    }

    public Duration calculateDuration() {
        return Duration.between(repairStartDate, repairFinishDate);
    }

    public void assignServiceRequest(ServiceRequest serviceRequest) {
        if (serviceRequest == null) {
            throw new IllegalArgumentException("Zgłoszenie jest wymagane.");
        }

        if (this.serviceRequest == serviceRequest) {
            return;
        }

        if (this.serviceRequest != null) {
            this.serviceRequest.removeRepair(this);
        }

        this.serviceRequest = serviceRequest;

        if (!serviceRequest.getRepairs().contains(this)) {
            serviceRequest.addRepair(this);
        }
    }

    public void removeServiceRequest() {
        if (this.serviceRequest != null) {
            ServiceRequest oldRequest = this.serviceRequest;
            this.serviceRequest = null;

            if (oldRequest.getRepairs().contains(this)) {
                oldRequest.removeRepair(this);
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

    public LocalDateTime getRepairStartDate() {
        return repairStartDate;
    }

    public LocalDateTime getRepairFinishDate() {
        return repairFinishDate;
    }

    public RepairResult getResult() {
        return result;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
}