package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pjatk.mas.coffeeservice.model.enums.MachineCondition;
import pl.pjatk.mas.coffeeservice.model.enums.RequestStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CoffeeMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numer seryjny jest wymagany.")
    @Column(nullable = false, unique = true)
    private String serialNumber;

    @NotNull(message = "Data rejestracji ekspresu jest wymagana.")
    @Column(nullable = false)
    private LocalDate registrationDate;

    @NotBlank(message = "Opis ekspresu jest wymagany.")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Stan techniczny ekspresu jest wymagany.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MachineCondition condition;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CoffeeMachineModel model;

    @OneToMany(mappedBy = "coffeeMachine")
    private List<ServiceRequest> serviceRequests = new ArrayList<>();

    protected CoffeeMachine() {
    }

    public CoffeeMachine(String serialNumber,
                         LocalDate registrationDate,
                         String description,
                         CoffeeMachineModel model) {
        this.serialNumber = requireText(serialNumber, "Numer seryjny jest wymagany.");
        this.registrationDate = requireNonNull(registrationDate, "Data rejestracji ekspresu jest wymagana.");
        this.description = requireText(description, "Opis ekspresu jest wymagany.");
        this.condition = MachineCondition.FUNCTIONAL;
        assignModel(model);
    }

    /**
     * Oblicza sugerowaną datę kolejnego przeglądu ekspresu na podstawie daty rejestracji
     * oraz zalecanego interwału przeglądów określonego w modelu ekspresu.
     *
     * @return sugerowana data kolejnego przeglądu lub {@code null}, jeśli brakuje wymaganych danych
     */
    public LocalDate calculateNextRecommendedServiceDate() {
        if (registrationDate == null || model == null) {
            return null;
        }

        return registrationDate.plusMonths(model.getRecommendedServiceIntervalMonths());
    }

    public void updateCondition(MachineCondition condition) {
        this.condition = requireNonNull(condition, "Stan techniczny ekspresu jest wymagany.");
    }

    public boolean hasActiveServiceRequest() {
        return serviceRequests.stream()
                .anyMatch(request -> request.getStatus() != RequestStatus.CLOSED);
    }

    private boolean hasActiveServiceRequestExcluding(ServiceRequest ignoredRequest) {
        return serviceRequests.stream()
                .filter(request -> request != ignoredRequest)
                .anyMatch(request -> request.getStatus() != RequestStatus.CLOSED);
    }

    /**
     * Dodaje zgłoszenie serwisowe do ekspresu.
     * Metoda pilnuje reguły, zgodnie z którą ekspres nie może mieć więcej niż jednego
     * aktywnego zgłoszenia serwisowego.
     *
     * @param request zgłoszenie serwisowe dotyczące ekspresu
     * @throws IllegalArgumentException gdy zgłoszenie jest puste
     * @throws IllegalStateException gdy ekspres ma już aktywne zgłoszenie
     */
    public void addServiceRequest(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (request.getStatus() != RequestStatus.CLOSED && hasActiveServiceRequestExcluding(request)) {
            throw new IllegalStateException("Ekspres ma już aktywne zgłoszenie serwisowe.");
        }

        if (!serviceRequests.contains(request)) {
            serviceRequests.add(request);
            request.assignCoffeeMachine(this);
        }
    }

    public void removeServiceRequest(ServiceRequest request) {
        if (request != null && serviceRequests.contains(request)) {
            serviceRequests.remove(request);
            request.removeCoffeeMachine();
        }
    }

    /**
     * Przypisuje ekspres do wskazanego klienta.
     * Metoda ustawia stronę właścicielską relacji {@code CoffeeMachine -> Customer},
     * która odpowiada za zapis klucza obcego w bazie danych.
     *
     * @param customer klient, do którego należy ekspres
     * @throws IllegalArgumentException gdy klient jest pusty
     */
    public void assignCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Klient jest wymagany.");
        }

        if (this.customer == customer) {
            return;
        }

        if (this.customer != null) {
            this.customer.removeCoffeeMachine(this);
        }

        this.customer = customer;

        if (!customer.getCoffeeMachines().contains(this)) {
            customer.addCoffeeMachine(this);
        }
    }

    public void removeCustomer() {
        if (this.customer != null) {
            Customer oldCustomer = this.customer;
            this.customer = null;

            if (oldCustomer.getCoffeeMachines().contains(this)) {
                oldCustomer.removeCoffeeMachine(this);
            }
        }
    }

    /**
     * Przypisuje ekspres do wskazanego modelu ekspresu.
     * Metoda ustawia stronę właścicielską relacji {@code CoffeeMachine -> CoffeeMachineModel}.
     *
     * @param model model ekspresu
     * @throws IllegalArgumentException gdy model jest pusty
     */
    public void assignModel(CoffeeMachineModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model ekspresu jest wymagany.");
        }

        if (this.model == model) {
            return;
        }

        if (this.model != null) {
            this.model.removeCoffeeMachine(this);
        }

        this.model = model;

        if (!model.getCoffeeMachines().contains(this)) {
            model.addCoffeeMachine(this);
        }
    }

    public void removeModel() {
        if (this.model != null) {
            CoffeeMachineModel oldModel = this.model;
            this.model = null;

            if (oldModel.getCoffeeMachines().contains(this)) {
                oldModel.removeCoffeeMachine(this);
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getDescription() {
        return description;
    }

    public MachineCondition getCondition() {
        return condition;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CoffeeMachineModel getModel() {
        return model;
    }

    public List<ServiceRequest> getServiceRequests() {
        return Collections.unmodifiableList(serviceRequests);
    }
}