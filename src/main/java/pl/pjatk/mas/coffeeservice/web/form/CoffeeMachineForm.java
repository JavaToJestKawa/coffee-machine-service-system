package pl.pjatk.mas.coffeeservice.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CoffeeMachineForm {

    @NotBlank(message = "Numer seryjny jest wymagany.")
    private String serialNumber;

    @NotBlank(message = "Opis ekspresu jest wymagany.")
    private String description;

    @NotNull(message = "Model ekspresu jest wymagany.")
    private Long modelId;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
}