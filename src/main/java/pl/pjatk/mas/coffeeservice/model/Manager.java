package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import pl.pjatk.mas.coffeeservice.model.enums.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Manager extends Employee {

    @NotBlank(message = "Obszar zarządzania jest wymagany.")
    @Column(nullable = false)
    private String managementArea;

    @OneToMany(mappedBy = "supervisingManager")
    private List<ServiceRequest> supervisedRequests = new ArrayList<>();

    protected Manager() {
    }

    public Manager(String firstName,
                   String lastName,
                   String middleName,
                   String phoneNumber,
                   String emailAddress,
                   BigDecimal salary,
                   LocalDate employmentDate,
                   boolean active,
                   String managementArea) {
        super(firstName, lastName, middleName, phoneNumber, emailAddress, salary, employmentDate, active);
        this.managementArea = requireText(managementArea, "Obszar zarządzania jest wymagany.");
    }

    /**
     * Przypisuje technika do zgłoszenia serwisowego oraz ustawia kierownika nadzorującego zgłoszenie.
     *
     * @param request zgłoszenie serwisowe
     * @param technician technik przypisywany do zgłoszenia
     * @throws IllegalArgumentException gdy zgłoszenie albo technik są puste
     */
    public void assignTechnicianToRequest(ServiceRequest request, Technician technician) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (technician == null) {
            throw new IllegalArgumentException("Technik nie może być pusty.");
        }

        request.assignTechnician(technician);
        request.assignSupervisingManager(this);

        if (request.getStatus() == RequestStatus.NEW) {
            request.changeStatus(RequestStatus.IN_PROGRESS);
        }
    }

    /**
     * Zamyka kompletne zgłoszenie serwisowe po sprawdzeniu wymaganych elementów procesu.
     *
     * @param request zamykane zgłoszenie serwisowe
     * @throws IllegalArgumentException gdy zgłoszenie jest puste
     * @throws IllegalStateException gdy zgłoszenie nie spełnia warunków zamknięcia
     */
    public void closeServiceRequest(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (request.getStatus() == RequestStatus.CLOSED) {
            throw new IllegalStateException("Zgłoszenie jest już zamknięte.");
        }

        if (request.getDiagnosis() == null) {
            throw new IllegalStateException("Nie można zamknąć zgłoszenia bez diagnozy.");
        }

        if (request.getServiceReport() == null) {
            throw new IllegalStateException("Nie można zamknąć zgłoszenia bez raportu serwisowego.");
        }

        if (request.getDiagnosis().isRepairPossible() && request.getRepairs().isEmpty()) {
            throw new IllegalStateException("Nie można zamknąć zgłoszenia bez naprawy.");
        }

        request.changeStatus(RequestStatus.CLOSED);
        request.setClosedAt(LocalDateTime.now());
    }

    public void addSupervisedRequest(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (!supervisedRequests.contains(request)) {
            supervisedRequests.add(request);
            request.assignSupervisingManager(this);
        }
    }

    public void removeSupervisedRequest(ServiceRequest request) {
        if (request != null && supervisedRequests.contains(request)) {
            supervisedRequests.remove(request);
            request.removeSupervisingManager();
        }
    }

    public String getManagementArea() {
        return managementArea;
    }

    public List<ServiceRequest> getSupervisedRequests() {
        return Collections.unmodifiableList(supervisedRequests);
    }
}