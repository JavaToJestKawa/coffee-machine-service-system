package pl.pjatk.mas.coffeeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.mas.coffeeservice.model.CoffeeMachineModel;

public interface CoffeeMachineModelRepository extends JpaRepository<CoffeeMachineModel, Long> {
}