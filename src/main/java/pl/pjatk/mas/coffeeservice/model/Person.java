package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Imię jest wymagane.")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane.")
    @Column(nullable = false)
    private String lastName;

    private String middleName;

    @NotBlank(message = "Numer telefonu jest wymagany.")
    @Column(nullable = false)
    private String phoneNumber;

    @Email(message = "Adres e-mail ma niepoprawny format.")
    @NotBlank(message = "Adres e-mail jest wymagany.")
    @Column(nullable = false)
    private String emailAddress;

    protected Person() {
    }

    protected Person(String firstName, String lastName, String middleName,
                     String phoneNumber, String emailAddress) {
        this.firstName = requireText(firstName, "Imię jest wymagane.");
        this.lastName = requireText(lastName, "Nazwisko jest wymagane.");
        this.middleName = normalizeNullable(middleName);
        this.phoneNumber = requireText(phoneNumber, "Numer telefonu jest wymagany.");
        this.emailAddress = requireEmail(emailAddress, "Adres e-mail jest wymagany.");
    }

    public String calculateFullName() {
        if (middleName == null || middleName.isBlank()) {
            return firstName + " " + lastName;
        }

        return firstName + " " + middleName + " " + lastName;
    }

    public void updateContactData(String phoneNumber, String emailAddress) {
        this.phoneNumber = requireText(phoneNumber, "Numer telefonu jest wymagany.");
        this.emailAddress = requireEmail(emailAddress, "Adres e-mail jest wymagany.");
    }

    protected String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    protected String requireEmail(String value, String message) {
        String email = requireText(value, message);

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Adres e-mail ma niepoprawny format.");
        }

        return email;
    }

    protected String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    protected <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}