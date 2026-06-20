package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public abstract class Employee extends Person {

    private static BigDecimal minSalary = new BigDecimal("4666.00");

    @NotNull(message = "Pensja jest wymagana.")
    @Column(nullable = false)
    @DecimalMin(value = "0.00", message = "Pensja nie może być ujemna.")
    private BigDecimal salary;

    @NotNull(message = "Data zatrudnienia jest wymagana.")
    @Column(nullable = false)
    private LocalDate employmentDate;

    @Column(nullable = false)
    private boolean active;

    protected Employee() {
    }

    protected Employee(String firstName,
                       String lastName,
                       String middleName,
                       String phoneNumber,
                       String emailAddress,
                       BigDecimal salary,
                       LocalDate employmentDate,
                       boolean active) {
        super(firstName, lastName, middleName, phoneNumber, emailAddress);
        this.employmentDate = requireNonNull(employmentDate, "Data zatrudnienia jest wymagana.");
        this.active = active;
        updateSalary(salary);
    }

    public void updateSalary(BigDecimal newSalary) {
        if (newSalary == null) {
            throw new IllegalArgumentException("Pensja jest wymagana.");
        }

        if (newSalary.compareTo(minSalary) < 0) {
            throw new IllegalArgumentException("Pensja nie może być niższa niż minimalna pensja.");
        }

        this.salary = newSalary;
    }

    public boolean checkAvailability() {
        return active;
    }

    public void deactivateEmployee() {
        this.active = false;
    }

    public void activateEmployee() {
        this.active = true;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public boolean isActive() {
        return active;
    }

    public static BigDecimal getMinSalary() {
        return minSalary;
    }

    public static void updateMinSalary(BigDecimal newMinSalary) {
        if (newMinSalary == null || newMinSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Minimalna pensja musi być dodatnia.");
        }

        minSalary = newMinSalary;
    }
}