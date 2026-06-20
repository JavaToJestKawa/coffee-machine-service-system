package pl.pjatk.mas.coffeeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.mas.coffeeservice.model.ServiceRequest;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
}