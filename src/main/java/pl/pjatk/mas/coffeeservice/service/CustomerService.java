package pl.pjatk.mas.coffeeservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.mas.coffeeservice.model.Customer;
import pl.pjatk.mas.coffeeservice.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Zwraca listę wszystkich klientów dostępnych w systemie.
     * Lista jest wykorzystywana w GUI jako pierwszy poziom nawigacji.
     *
     * @return lista klientów
     */
    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Pobiera klienta razem z jego zarejestrowanymi ekspresami.
     * Metoda jest wykorzystywana w GUI do przejścia po asocjacji
     * {@code Customer -> CoffeeMachine} i wyświetlenia ekspresów wybranego klienta.
     *
     * @param customerId identyfikator klienta
     * @return klient z załadowaną kolekcją ekspresów
     * @throws IllegalArgumentException gdy klient o podanym identyfikatorze nie istnieje
     */
    @Transactional(readOnly = true)
    public Customer findCustomerWithMachines(Long customerId) {
        return customerRepository.findWithCoffeeMachinesById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta."));
    }

    /**
     * Pobiera pojedynczego klienta na podstawie identyfikatora.
     *
     * @param customerId identyfikator klienta
     * @return znaleziony klient
     * @throws IllegalArgumentException gdy klient o podanym identyfikatorze nie istnieje
     */
    @Transactional(readOnly = true)
    public Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono klienta."));
    }
}