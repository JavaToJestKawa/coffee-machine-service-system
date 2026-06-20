package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Customer extends Person {

    @NotNull(message = "Data rejestracji klienta jest wymagana.")
    @Column(nullable = false)
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "customer")
    private List<CoffeeMachine> coffeeMachines = new ArrayList<>();

    protected Customer() {
    }

    public Customer(String firstName,
                    String lastName,
                    String middleName,
                    String phoneNumber,
                    String emailAddress,
                    LocalDate registrationDate) {
        super(firstName, lastName, middleName, phoneNumber, emailAddress);
        this.registrationDate = requireNonNull(registrationDate, "Data rejestracji klienta jest wymagana.");
    }

    /**
     * Dodaje ekspres do kolekcji ekspresów klienta i tworzy powiązanie zwrotne
     * pomiędzy ekspresem a klientem.
     *
     * @param machine ekspres przypisywany do klienta
     * @throws IllegalArgumentException gdy ekspres jest pusty
     */
    public void addCoffeeMachine(CoffeeMachine machine) {
        if (machine == null) {
            throw new IllegalArgumentException("Ekspres nie może być pusty.");
        }

        if (!coffeeMachines.contains(machine)) {
            coffeeMachines.add(machine);
            machine.assignCustomer(this);
        }
    }

    public void removeCoffeeMachine(CoffeeMachine machine) {
        if (machine != null && coffeeMachines.contains(machine)) {
            coffeeMachines.remove(machine);
            machine.removeCustomer();
        }
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public List<CoffeeMachine> getCoffeeMachines() {
        return Collections.unmodifiableList(coffeeMachines);
    }
}