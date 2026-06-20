package pl.pjatk.mas.coffeeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.mas.coffeeservice.model.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
}