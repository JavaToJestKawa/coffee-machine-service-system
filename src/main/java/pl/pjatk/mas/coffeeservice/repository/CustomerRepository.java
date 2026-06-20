package pl.pjatk.mas.coffeeservice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.mas.coffeeservice.model.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph(attributePaths = {
            "coffeeMachines",
            "coffeeMachines.model",
            "coffeeMachines.model.manufacturer"
    })
    Optional<Customer> findWithCoffeeMachinesById(Long id);
}