package pl.pjatk.mas.coffeeservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pl.pjatk.mas.coffeeservice.model.enums.MachineType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CoffeeMachineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa modelu jest wymagana.")
    @Column(nullable = false)
    private String modelName;

    @NotNull(message = "Typ ekspresu jest wymagany.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MachineType machineType;

    @Min(value = 1, message = "Zalecany interwał przeglądów musi być dodatni.")
    @Column(nullable = false)
    private int recommendedServiceIntervalMonths;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Manufacturer manufacturer;

    @OneToMany(mappedBy = "model")
    private List<CoffeeMachine> coffeeMachines = new ArrayList<>();

    protected CoffeeMachineModel() {
    }

    public CoffeeMachineModel(String modelName,
                              MachineType machineType,
                              int recommendedServiceIntervalMonths) {
        this.modelName = requireText(modelName, "Nazwa modelu jest wymagana.");

        if (machineType == null) {
            throw new IllegalArgumentException("Typ ekspresu jest wymagany.");
        }

        if (recommendedServiceIntervalMonths <= 0) {
            throw new IllegalArgumentException("Zalecany interwał przeglądów musi być dodatni.");
        }

        this.machineType = machineType;
        this.recommendedServiceIntervalMonths = recommendedServiceIntervalMonths;
    }

    public void assignManufacturer(Manufacturer manufacturer) {
        if (manufacturer == null) {
            throw new IllegalArgumentException("Producent jest wymagany.");
        }

        if (this.manufacturer == manufacturer) {
            return;
        }

        if (this.manufacturer != null) {
            this.manufacturer.removeModel(this);
        }

        this.manufacturer = manufacturer;

        if (!manufacturer.getModels().contains(this)) {
            manufacturer.addModel(this);
        }
    }

    public void removeManufacturer() {
        if (this.manufacturer != null) {
            Manufacturer oldManufacturer = this.manufacturer;
            this.manufacturer = null;

            if (oldManufacturer.getModels().contains(this)) {
                oldManufacturer.removeModel(this);
            }
        }
    }

    public void addCoffeeMachine(CoffeeMachine machine) {
        if (machine == null) {
            throw new IllegalArgumentException("Ekspres nie może być pusty.");
        }

        if (!coffeeMachines.contains(machine)) {
            coffeeMachines.add(machine);
            machine.assignModel(this);
        }
    }

    public void removeCoffeeMachine(CoffeeMachine machine) {
        if (machine != null && coffeeMachines.contains(machine)) {
            coffeeMachines.remove(machine);
            machine.removeModel();
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }

    public Long getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    public int getRecommendedServiceIntervalMonths() {
        return recommendedServiceIntervalMonths;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public List<CoffeeMachine> getCoffeeMachines() {
        return Collections.unmodifiableList(coffeeMachines);
    }

    public String getDisplayName() {
        if (manufacturer == null) {
            return modelName + " (" + machineType + ")";
        }

        return manufacturer.getName() + " " + modelName + " (" + machineType + ")";
    }
}