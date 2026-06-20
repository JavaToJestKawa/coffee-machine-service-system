package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import pl.pjatk.mas.coffeeservice.model.enums.MachineCondition;
import pl.pjatk.mas.coffeeservice.model.enums.RepairResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Technician extends Employee {

    @NotBlank(message = "Specjalizacja jest wymagana.")
    @Column(nullable = false)
    private String specialization;

    @ElementCollection
    @CollectionTable(
            name = "technician_certificates",
            joinColumns = @JoinColumn(name = "technician_id")
    )
    @Column(name = "certificate")
    private Set<String> certificates = new HashSet<>();

    @OneToMany(mappedBy = "assignedTechnician")
    private List<ServiceRequest> assignedRequests = new ArrayList<>();

    protected Technician() {
    }

    public Technician(String firstName,
                      String lastName,
                      String middleName,
                      String phoneNumber,
                      String emailAddress,
                      BigDecimal salary,
                      LocalDate employmentDate,
                      boolean active,
                      String specialization,
                      Collection<String> certificates) {
        super(firstName, lastName, middleName, phoneNumber, emailAddress, salary, employmentDate, active);
        this.specialization = requireText(specialization, "Specjalizacja jest wymagana.");

        if (certificates != null) {
            certificates.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .forEach(this.certificates::add);
        }
    }

    /**
     * Wykonuje diagnozę zgłoszenia przypisanego do technika.
     * Utworzona diagnoza zostaje przypisana do zgłoszenia serwisowego.
     *
     * @param request diagnozowane zgłoszenie
     * @param faultDescription opis wykrytej usterki
     * @param estimatedCost szacowany koszt naprawy
     * @param repairPossible informacja, czy naprawa jest możliwa
     * @return utworzona diagnoza
     * @throws IllegalStateException gdy zgłoszenie nie jest przypisane do tego technika
     */
    public Diagnosis performDiagnosis(ServiceRequest request,
                                      String faultDescription,
                                      BigDecimal estimatedCost,
                                      boolean repairPossible) {
        ensureAssignedTo(request);

        Diagnosis diagnosis = new Diagnosis(
                LocalDateTime.now(),
                faultDescription,
                estimatedCost,
                repairPossible
        );

        request.attachDiagnosis(diagnosis);
        return diagnosis;
    }

    /**
     * Wykonuje naprawę zgłoszenia przypisanego do technika.
     * Metoda wymaga wcześniejszej diagnozy oraz aktualizuje stan ekspresu na podstawie wyniku naprawy.
     *
     * @param request naprawiane zgłoszenie
     * @param repairStartDate data rozpoczęcia naprawy
     * @param repairFinishDate data zakończenia naprawy
     * @param description opis wykonanych prac
     * @param cost koszt naprawy
     * @param result wynik naprawy
     * @return utworzona naprawa
     * @throws IllegalStateException gdy zgłoszenie nie może zostać naprawione
     */
    public Repair performRepair(ServiceRequest request,
                                LocalDateTime repairStartDate,
                                LocalDateTime repairFinishDate,
                                String description,
                                BigDecimal cost,
                                RepairResult result) {
        ensureAssignedTo(request);

        if (request.getDiagnosis() == null) {
            throw new IllegalStateException("Nie można wykonać naprawy bez diagnozy.");
        }

        if (!request.getDiagnosis().isRepairPossible()) {
            throw new IllegalStateException("Diagnoza wskazuje brak możliwości naprawy.");
        }

        Repair repair = new Repair(repairStartDate, repairFinishDate, result, description, cost);
        request.addRepair(repair);

        if (result == RepairResult.SUCCESSFUL) {
            request.getCoffeeMachine().updateCondition(MachineCondition.FUNCTIONAL);
        } else {
            request.getCoffeeMachine().updateCondition(MachineCondition.FAULTY);
        }

        return repair;
    }

    /**
     * Przygotowuje raport serwisowy dla zgłoszenia przypisanego do technika.
     *
     * @param request zgłoszenie, dla którego przygotowywany jest raport
     * @param workSummary opis wykonanych prac
     * @param recommendations zalecenia serwisowe
     * @return utworzony raport serwisowy
     * @throws IllegalStateException gdy zgłoszenie nie ma diagnozy lub nie jest przypisane do tego technika
     */
    public ServiceReport prepareServiceReport(ServiceRequest request,
                                              String workSummary,
                                              String recommendations) {
        ensureAssignedTo(request);

        if (request.getDiagnosis() == null) {
            throw new IllegalStateException("Nie można przygotować raportu bez diagnozy.");
        }

        ServiceReport report = new ServiceReport(LocalDateTime.now(), workSummary, recommendations);
        request.attachServiceReport(report);
        return report;
    }

    public void addAssignedRequest(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (!assignedRequests.contains(request)) {
            assignedRequests.add(request);
            request.assignTechnician(this);
        }
    }

    public void removeAssignedRequest(ServiceRequest request) {
        if (request != null && assignedRequests.contains(request)) {
            assignedRequests.remove(request);
            request.removeAssignedTechnician();
        }
    }
    private void ensureAssignedTo(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (request.getAssignedTechnician() != this) {
            throw new IllegalStateException("Technik może obsługiwać tylko zgłoszenia przypisane do siebie.");
        }
    }

    public String getSpecialization() {
        return specialization;
    }

    public Set<String> getCertificates() {
        return Collections.unmodifiableSet(certificates);
    }

    public List<ServiceRequest> getAssignedRequests() {
        return Collections.unmodifiableList(assignedRequests);
    }
}