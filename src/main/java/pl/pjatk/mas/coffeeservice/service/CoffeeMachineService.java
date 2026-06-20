package pl.pjatk.mas.coffeeservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.mas.coffeeservice.model.CoffeeMachine;
import pl.pjatk.mas.coffeeservice.model.CoffeeMachineModel;
import pl.pjatk.mas.coffeeservice.model.Customer;
import pl.pjatk.mas.coffeeservice.repository.CoffeeMachineModelRepository;
import pl.pjatk.mas.coffeeservice.repository.CoffeeMachineRepository;
import pl.pjatk.mas.coffeeservice.repository.CustomerRepository;
import pl.pjatk.mas.coffeeservice.web.form.CoffeeMachineForm;

import java.time.LocalDate;
import java.util.List;

@Service
public class CoffeeMachineService {

    private final CoffeeMachineRepository coffeeMachineRepository;
    private final CoffeeMachineModelRepository modelRepository;
    private final CustomerRepository customerRepository;

    public CoffeeMachineService(CoffeeMachineRepository coffeeMachineRepository,
                                CoffeeMachineModelRepository modelRepository,
                                CustomerRepository customerRepository) {
        this.coffeeMachineRepository = coffeeMachineRepository;
        this.modelRepository = modelRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Zwraca wszystkie dostępne modele ekspresów, które mogą zostać wybrane podczas rejestracji ekspresu.
     *
     * @return lista modeli ekspresów
     */
    @Transactional(readOnly = true)
    public List<CoffeeMachineModel> findAllModels() {
        return modelRepository.findAll();
    }

    /**
     * Rejestruje nowy ekspres dla wskazanego klienta.
     * Metoda pobiera klienta i model ekspresu z bazy, sprawdza unikalność numeru seryjnego,
     * tworzy obiekt CoffeeMachine, przypisuje go do klienta i zapisuje w bazie danych.
     *
     * @param customerId identyfikator klienta
     * @param form dane formularza rejestracji ekspresu
     * @return zapisany ekspres
     * @throws IllegalArgumentException gdy klient, model lub numer seryjny są niepoprawne
     */
    @Transactional
    public CoffeeMachine registerCoffeeMachine(Long customerId, CoffeeMachineForm form) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta."));

        CoffeeMachineModel model = modelRepository.findById(form.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono modelu ekspresu."));

        if (coffeeMachineRepository.existsBySerialNumberIgnoreCase(form.getSerialNumber())) {
            throw new IllegalArgumentException("Ekspres o podanym numerze seryjnym już istnieje.");
        }

        CoffeeMachine machine = new CoffeeMachine(
                form.getSerialNumber(),
                LocalDate.now(),
                form.getDescription(),
                model
        );

        customer.addCoffeeMachine(machine);

        return coffeeMachineRepository.save(machine);
    }
}