package pl.pjatk.mas.coffeeservice.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.mas.coffeeservice.model.Customer;
import pl.pjatk.mas.coffeeservice.service.CoffeeMachineService;
import pl.pjatk.mas.coffeeservice.service.CustomerService;
import pl.pjatk.mas.coffeeservice.web.form.CoffeeMachineForm;

@Controller
@RequestMapping("/customers")
public class CoffeeMachineController {

    private final CustomerService customerService;
    private final CoffeeMachineService coffeeMachineService;

    public CoffeeMachineController(CustomerService customerService,
                                   CoffeeMachineService coffeeMachineService) {
        this.customerService = customerService;
        this.coffeeMachineService = coffeeMachineService;
    }

    @GetMapping
    public String showCustomers(Model model) {
        model.addAttribute("customers", customerService.findAllCustomers());
        model.addAttribute("selectedCustomer", null);
        return "customers";
    }

    @GetMapping("/{customerId}")
    public String showCustomerMachines(@PathVariable Long customerId,
                                       @RequestParam(required = false) Boolean registered,
                                       Model model) {
        Customer selectedCustomer = customerService.findCustomerWithMachines(customerId);

        model.addAttribute("customers", customerService.findAllCustomers());
        model.addAttribute("selectedCustomer", selectedCustomer);
        model.addAttribute("registered", Boolean.TRUE.equals(registered));

        return "customers";
    }

    @GetMapping("/{customerId}/machines/new")
    public String showRegisterMachineForm(@PathVariable Long customerId,
                                          Model model) {
        Customer customer = customerService.findCustomer(customerId);

        model.addAttribute("customer", customer);
        model.addAttribute("machineForm", new CoffeeMachineForm());
        model.addAttribute("machineModels", coffeeMachineService.findAllModels());

        return "register-machine";
    }

    @PostMapping("/{customerId}/machines")
    public String registerMachine(@PathVariable Long customerId,
                                  @Valid @ModelAttribute("machineForm") CoffeeMachineForm machineForm,
                                  BindingResult bindingResult,
                                  Model model) {
        Customer customer = customerService.findCustomer(customerId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("customer", customer);
            model.addAttribute("machineModels", coffeeMachineService.findAllModels());
            return "register-machine";
        }

        try {
            coffeeMachineService.registerCoffeeMachine(customerId, machineForm);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("customer", customer);
            model.addAttribute("machineModels", coffeeMachineService.findAllModels());
            model.addAttribute("errorMessage", exception.getMessage());
            return "register-machine";
        }

        return "redirect:/customers/" + customerId + "?registered=true";
    }
}

///customers
//→ lista klientów
//
///customers/{id}
//        → klient + jego ekspresy
//
///customers/{id}/machines/new
//        → formularz rejestracji ekspresu
//
//POST /customers/{id}/machines
//→ zapis ekspresu
//→ redirect do listy ekspresów klienta