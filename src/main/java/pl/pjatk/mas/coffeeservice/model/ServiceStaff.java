package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class ServiceStaff extends Employee {

    @NotBlank(message = "Numer biurka jest wymagany.")
    @Column(nullable = false)
    private String deskNumber;

    @OneToMany(mappedBy = "acceptedBy")
    private List<ServiceRequest> acceptedRequests = new ArrayList<>();

    protected ServiceStaff() {
    }

    public ServiceStaff(String firstName,
                        String lastName,
                        String middleName,
                        String phoneNumber,
                        String emailAddress,
                        BigDecimal salary,
                        LocalDate employmentDate,
                        boolean active,
                        String deskNumber) {
        super(firstName, lastName, middleName, phoneNumber, emailAddress, salary, employmentDate, active);
        this.deskNumber = requireText(deskNumber, "Numer biurka jest wymagany.");
    }

    public Customer registerCustomer(String firstName,
                                     String lastName,
                                     String middleName,
                                     String phoneNumber,
                                     String emailAddress,
                                     LocalDate registrationDate) {
        return new Customer(
                firstName,
                lastName,
                middleName,
                phoneNumber,
                emailAddress,
                registrationDate
        );
    }

    //    W obecnym GUI konkretny ServiceStaff nie jest wybierany.
    /**
     * Przypisuje rejestrowany ekspres do wskazanego klienta.
     *
     * @param customer klient, do którego przypisywany jest ekspres
     * @param machine rejestrowany ekspres
     * @throws IllegalArgumentException gdy klient albo ekspres jest pusty
     */
    public void registerCoffeeMachine(Customer customer, CoffeeMachine machine) {
        if (customer == null) {
            throw new IllegalArgumentException("Klient nie może być pusty.");
        }

        if (machine == null) {
            throw new IllegalArgumentException("Ekspres nie może być pusty.");
        }

        customer.addCoffeeMachine(machine);
    }

    public void registerServiceRequest(CoffeeMachine machine, ServiceRequest request) {
        if (machine == null) {
            throw new IllegalArgumentException("Ekspres nie może być pusty.");
        }

        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        machine.addServiceRequest(request);
        addAcceptedRequest(request);
    }

    public void addAcceptedRequest(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Zgłoszenie nie może być puste.");
        }

        if (!acceptedRequests.contains(request)) {
            acceptedRequests.add(request);
            request.assignAcceptedBy(this);
        }
    }

    public void removeAcceptedRequest(ServiceRequest request) {
        if (request != null && acceptedRequests.contains(request)) {
            acceptedRequests.remove(request);
            request.removeAcceptedBy();
        }
    }

    public String getDeskNumber() {
        return deskNumber;
    }

    public List<ServiceRequest> getAcceptedRequests() {
        return Collections.unmodifiableList(acceptedRequests);
    }
}