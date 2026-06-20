package pl.pjatk.mas.coffeeservice.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.mas.coffeeservice.model.*;
import pl.pjatk.mas.coffeeservice.model.enums.MachineType;
import pl.pjatk.mas.coffeeservice.repository.CoffeeMachineModelRepository;
import pl.pjatk.mas.coffeeservice.repository.CoffeeMachineRepository;
import pl.pjatk.mas.coffeeservice.repository.CustomerRepository;
import pl.pjatk.mas.coffeeservice.repository.ManufacturerRepository;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final CoffeeMachineModelRepository modelRepository;
    private final CoffeeMachineRepository coffeeMachineRepository;

    public DataInitializer(CustomerRepository customerRepository,
                           ManufacturerRepository manufacturerRepository,
                           CoffeeMachineModelRepository modelRepository,
                           CoffeeMachineRepository coffeeMachineRepository) {
        this.customerRepository = customerRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.modelRepository = modelRepository;
        this.coffeeMachineRepository = coffeeMachineRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Manufacturer delonghi = new Manufacturer(
                "DeLonghi",
                "Włochy",
                "support@delonghi.example.com",
                "123456789"
        );

        Manufacturer jura = new Manufacturer(
                "Jura",
                "Szwajcaria",
                "support@jura.example.com",
                "987654321"
        );

        Manufacturer nespresso = new Manufacturer(
                "Nespresso",
                "Szwajcaria",
                "support@nespresso.example.com",
                "222333444"
        );

        Manufacturer rancilio = new Manufacturer(
                "Rancilio",
                "Włochy",
                "support@rancilio.example.com",
                "555666777"
        );

        Manufacturer bosch = new Manufacturer(
                "Bosch",
                "Niemcy",
                "support@bosch.example.com",
                "111222333"
        );

        manufacturerRepository.save(delonghi);
        manufacturerRepository.save(jura);
        manufacturerRepository.save(nespresso);
        manufacturerRepository.save(rancilio);
        manufacturerRepository.save(bosch);

        CoffeeMachineModel delonghiMagnifica = new CoffeeMachineModel(
                "Magnifica S ECAM 22.110",
                MachineType.AUTOMATIC,
                12
        );

        CoffeeMachineModel delonghiDinamica = new CoffeeMachineModel(
                "Dinamica Plus ECAM 370.95",
                MachineType.AUTOMATIC,
                12
        );

        CoffeeMachineModel juraE8 = new CoffeeMachineModel(
                "E8",
                MachineType.AUTOMATIC,
                12
        );

        CoffeeMachineModel juraS8 = new CoffeeMachineModel(
                "S8",
                MachineType.AUTOMATIC,
                12
        );

        CoffeeMachineModel nespressoEssenza = new CoffeeMachineModel(
                "Essenza Mini",
                MachineType.CAPSULE,
                18
        );

        CoffeeMachineModel nespressoVertuo = new CoffeeMachineModel(
                "Vertuo Next",
                MachineType.CAPSULE,
                18
        );

        CoffeeMachineModel nespressoLattissima = new CoffeeMachineModel(
                "Lattissima One",
                MachineType.CAPSULE,
                18
        );

        CoffeeMachineModel rancilioSilvia = new CoffeeMachineModel(
                "Silvia Pro X",
                MachineType.PORTAFILTER,
                6
        );

        CoffeeMachineModel rancilioClasse5 = new CoffeeMachineModel(
                "Classe 5",
                MachineType.PORTAFILTER,
                6
        );

        CoffeeMachineModel boschBuiltIn = new CoffeeMachineModel(
                "Serie 8 Built-In",
                MachineType.BUILT_IN,
                12
        );

        delonghi.addModel(delonghiMagnifica);
        delonghi.addModel(delonghiDinamica);

        jura.addModel(juraE8);
        jura.addModel(juraS8);

        nespresso.addModel(nespressoEssenza);
        nespresso.addModel(nespressoVertuo);
        nespresso.addModel(nespressoLattissima);

        rancilio.addModel(rancilioSilvia);
        rancilio.addModel(rancilioClasse5);

        bosch.addModel(boschBuiltIn);

        modelRepository.save(delonghiMagnifica);
        modelRepository.save(delonghiDinamica);
        modelRepository.save(juraE8);
        modelRepository.save(juraS8);
        modelRepository.save(nespressoEssenza);
        modelRepository.save(nespressoVertuo);
        modelRepository.save(nespressoLattissima);
        modelRepository.save(rancilioSilvia);
        modelRepository.save(rancilioClasse5);
        modelRepository.save(boschBuiltIn);

        Customer customer1 = new Customer(
                "Vincent",
                "Vega",
                null,
                "500100200",
                "vincent.vega@example.com",
                LocalDate.now()
        );

        Customer customer2 = new Customer(
                "Ryszard",
                "Ochódzki",
                null,
                "500300400",
                "ryszard.ochodzki@example.com",
                LocalDate.now()
        );

        Customer customer3 = new Customer(
                "Ebenezer",
                "Scrooge",
                null,
                "500500600",
                "ebenezer.scrooge@example.com",
                LocalDate.now()
        );

        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);

        CoffeeMachine machine1 = new CoffeeMachine(
                "VV-ECAM-001",
                LocalDate.now(),
                "Automatyczny ekspres domowy klienta.",
                delonghiMagnifica
        );

        CoffeeMachine machine2 = new CoffeeMachine(
                "RO-JURA-001",
                LocalDate.now(),
                "Ekspres automatyczny używany w biurze.",
                juraE8
        );

        CoffeeMachine machine3 = new CoffeeMachine(
                "ES-NESP-001",
                LocalDate.now(),
                "Kapsułkowy ekspres do użytku prywatnego.",
                nespressoVertuo
        );

        CoffeeMachine machine4 = new CoffeeMachine(
                "ES-RANC-001",
                LocalDate.now(),
                "Kolbowy ekspres wykorzystywany w lokalu.",
                rancilioClasse5
        );

        CoffeeMachine machine5 = new CoffeeMachine(
                "RO-BOSCH-001",
                LocalDate.now(),
                "Ekspres do zabudowy używany w biurze.",
                boschBuiltIn
        );

        customer1.addCoffeeMachine(machine1);
        customer2.addCoffeeMachine(machine2);
        customer3.addCoffeeMachine(machine3);
        customer3.addCoffeeMachine(machine4);
        customer2.addCoffeeMachine(machine5);

        coffeeMachineRepository.save(machine1);
        coffeeMachineRepository.save(machine2);
        coffeeMachineRepository.save(machine3);
        coffeeMachineRepository.save(machine4);
        coffeeMachineRepository.save(machine5);
    }
}