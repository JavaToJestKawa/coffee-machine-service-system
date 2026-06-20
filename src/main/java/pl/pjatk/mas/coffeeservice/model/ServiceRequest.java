package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pjatk.mas.coffeeservice.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data zgłoszenia jest wymagana.")
    @Column(nullable = false)
    private LocalDateTime requestDate;

    @NotBlank(message = "Opis problemu jest wymagany.")
    @Column(nullable = false)
    private String problemDescription;

    @NotNull(message = "Status zgłoszenia jest wymagany.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    private LocalDateTime closedAt;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CoffeeMachine coffeeMachine;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private ServiceStaff acceptedBy;

    @ManyToOne
    private Manager supervisingManager;

    @ManyToOne
    private Technician assignedTechnician;

    @OneToOne(mappedBy = "serviceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Diagnosis diagnosis;

    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repair> repairs = new ArrayList<>();

    @OneToOne(mappedBy = "serviceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private ServiceReport serviceReport;

    protected ServiceRequest() {
    }

    public ServiceRequest(LocalDateTime requestDate, String problemDescription) {
        this.requestDate = requireNonNull(requestDate, "Data zgłoszenia jest wymagana.");
        this.problemDescription = requireText(problemDescription, "Opis problemu jest wymagany.");
        this.status = RequestStatus.NEW;
    }

    public void assignCoffeeMachine(CoffeeMachine coffeeMachine) {
        if (coffeeMachine == null) {
            throw new IllegalArgumentException("Ekspres jest wymagany.");
        }

        if (this.coffeeMachine == coffeeMachine) {
            return;
        }

        if (this.coffeeMachine != null) {
            this.coffeeMachine.removeServiceRequest(this);
        }

        this.coffeeMachine = coffeeMachine;

        if (!coffeeMachine.getServiceRequests().contains(this)) {
            coffeeMachine.addServiceRequest(this);
        }
    }

    public void removeCoffeeMachine() {
        if (this.coffeeMachine != null) {
            CoffeeMachine oldMachine = this.coffeeMachine;
            this.coffeeMachine = null;

            if (oldMachine.getServiceRequests().contains(this)) {
                oldMachine.removeServiceRequest(this);
            }
        }
    }

    public void assignAcceptedBy(ServiceStaff acceptedBy) {
        if (acceptedBy == null) {
            throw new IllegalArgumentException("Pracownik obsługi jest wymagany.");
        }

        if (this.acceptedBy == acceptedBy) {
            return;
        }

        if (this.acceptedBy != null) {
            this.acceptedBy.removeAcceptedRequest(this);
        }

        this.acceptedBy = acceptedBy;

        if (!acceptedBy.getAcceptedRequests().contains(this)) {
            acceptedBy.addAcceptedRequest(this);
        }
    }

    public void removeAcceptedBy() {
        if (this.acceptedBy != null) {
            ServiceStaff oldAcceptedBy = this.acceptedBy;
            this.acceptedBy = null;

            if (oldAcceptedBy.getAcceptedRequests().contains(this)) {
                oldAcceptedBy.removeAcceptedRequest(this);
            }
        }
    }

    public void assignSupervisingManager(Manager manager) {
        if (this.supervisingManager == manager) {
            return;
        }

        if (this.supervisingManager != null) {
            this.supervisingManager.removeSupervisedRequest(this);
        }

        this.supervisingManager = manager;

        if (manager != null && !manager.getSupervisedRequests().contains(this)) {
            manager.addSupervisedRequest(this);
        }
    }

    public void removeSupervisingManager() {
        if (this.supervisingManager != null) {
            Manager oldManager = this.supervisingManager;
            this.supervisingManager = null;

            if (oldManager.getSupervisedRequests().contains(this)) {
                oldManager.removeSupervisedRequest(this);
            }
        }
    }

    public void assignTechnician(Technician technician) {
        if (technician == null) {
            throw new IllegalArgumentException("Technik jest wymagany.");
        }

        if (this.assignedTechnician == technician) {
            return;
        }

        if (this.assignedTechnician != null) {
            this.assignedTechnician.removeAssignedRequest(this);
        }

        this.assignedTechnician = technician;

        if (!technician.getAssignedRequests().contains(this)) {
            technician.addAssignedRequest(this);
        }
    }

    public void removeAssignedTechnician() {
        if (this.assignedTechnician != null) {
            Technician oldTechnician = this.assignedTechnician;
            this.assignedTechnician = null;

            if (oldTechnician.getAssignedRequests().contains(this)) {
                oldTechnician.removeAssignedRequest(this);
            }
        }
    }

    /**
     * Przypisuje diagnozę do zgłoszenia serwisowego.
     * Zgłoszenie może mieć maksymalnie jedną diagnozę.
     *
     * @param diagnosis diagnoza przypisywana do zgłoszenia
     * @throws IllegalArgumentException gdy diagnoza jest pusta
     * @throws IllegalStateException gdy zgłoszenie ma już przypisaną diagnozę
     */
    public void attachDiagnosis(Diagnosis diagnosis) {
        if (diagnosis == null) {
            throw new IllegalArgumentException("Diagnoza nie może być pusta.");
        }

        if (this.diagnosis == diagnosis) {
            return;
        }

        if (this.diagnosis != null) {
            throw new IllegalStateException("Zgłoszenie ma już diagnozę.");
        }

        this.diagnosis = diagnosis;

        if (diagnosis.getServiceRequest() != this) {
            diagnosis.assignServiceRequest(this);
        }

        changeStatus(RequestStatus.IN_PROGRESS);
    }

    public void removeDiagnosis() {
        if (this.diagnosis != null) {
            Diagnosis oldDiagnosis = this.diagnosis;
            this.diagnosis = null;

            if (oldDiagnosis.getServiceRequest() == this) {
                oldDiagnosis.removeServiceRequest();
            }
        }
    }

    /**
     * Dodaje naprawę do zgłoszenia serwisowego i zmienia status zgłoszenia na w toku.
     *
     * @param repair naprawa dodawana do zgłoszenia
     * @throws IllegalArgumentException gdy naprawa jest pusta
     */
    public void addRepair(Repair repair) {
        if (repair == null) {
            throw new IllegalArgumentException("Naprawa nie może być pusta.");
        }

        if (!repairs.contains(repair)) {
            repairs.add(repair);
            repair.assignServiceRequest(this);
        }

        changeStatus(RequestStatus.IN_PROGRESS);
    }

    public void removeRepair(Repair repair) {
        if (repair != null && repairs.contains(repair)) {
            repairs.remove(repair);
            repair.removeServiceRequest();
        }
    }

    /**
     * Przypisuje raport serwisowy do zgłoszenia.
     * Zgłoszenie może mieć maksymalnie jeden raport serwisowy.
     *
     * @param report raport przypisywany do zgłoszenia
     * @throws IllegalArgumentException gdy raport jest pusty
     * @throws IllegalStateException gdy zgłoszenie ma już przypisany raport
     */
    public void attachServiceReport(ServiceReport report) {
        if (report == null) {
            throw new IllegalArgumentException("Raport nie może być pusty.");
        }

        if (this.serviceReport == report) {
            return;
        }

        if (this.serviceReport != null) {
            throw new IllegalStateException("Zgłoszenie ma już raport serwisowy.");
        }

        this.serviceReport = report;

        if (report.getServiceRequest() != this) {
            report.assignServiceRequest(this);
        }
    }

    public void removeServiceReport() {
        if (this.serviceReport != null) {
            ServiceReport oldReport = this.serviceReport;
            this.serviceReport = null;

            if (oldReport.getServiceRequest() == this) {
                oldReport.removeServiceRequest();
            }
        }
    }

    public void changeStatus(RequestStatus status) {
        this.status = requireNonNull(status, "Status zgłoszenia jest wymagany.");
    }

    void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
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

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public CoffeeMachine getCoffeeMachine() {
        return coffeeMachine;
    }

    public ServiceStaff getAcceptedBy() {
        return acceptedBy;
    }

    public Manager getSupervisingManager() {
        return supervisingManager;
    }

    public Technician getAssignedTechnician() {
        return assignedTechnician;
    }

    public Diagnosis getDiagnosis() {
        return diagnosis;
    }

    public List<Repair> getRepairs() {
        return Collections.unmodifiableList(repairs);
    }

    public ServiceReport getServiceReport() {
        return serviceReport;
    }
}