package pl.pjatk.mas.coffeeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.mas.coffeeservice.model.CoffeeMachine;

import java.util.List;

public interface CoffeeMachineRepository extends JpaRepository<CoffeeMachine, Long> {

    boolean existsBySerialNumberIgnoreCase(String serialNumber);

////    USUNAC?
//    List<CoffeeMachine> findByCustomerId(Long customerId);
}