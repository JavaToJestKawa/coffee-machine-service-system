package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa producenta jest wymagana.")
    @Column(nullable = false)
    private String name;

    private String country;

    @Email(message = "Adres e-mail wsparcia ma niepoprawny format.")
    private String supportEmail;

    private String supportPhone;

    @OneToMany(mappedBy = "manufacturer")
    private List<CoffeeMachineModel> models = new ArrayList<>();

    protected Manufacturer() {
    }

    public Manufacturer(String name,
                        String country,
                        String supportEmail,
                        String supportPhone) {
        this.name = requireText(name, "Nazwa producenta jest wymagana.");
        this.country = normalizeNullable(country);
        this.supportEmail = normalizeNullable(supportEmail);
        this.supportPhone = normalizeNullable(supportPhone);
    }

    public void addModel(CoffeeMachineModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model ekspresu nie może być pusty.");
        }

        if (!models.contains(model)) {
            models.add(model);
            model.assignManufacturer(this);
        }
    }

    public void removeModel(CoffeeMachineModel model) {
        if (model != null && models.contains(model)) {
            models.remove(model);
            model.removeManufacturer();
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public List<CoffeeMachineModel> getModels() {
        return Collections.unmodifiableList(models);
    }
}