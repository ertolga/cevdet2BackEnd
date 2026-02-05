package com.tolga.customer.controller;

import com.tolga.customer.dto.CustomerRequest;
import com.tolga.customer.dto.CustomerResponse;
import com.tolga.customer.model.Customer;
import com.tolga.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.tolga.customer.dto.UpdateIbanRequest;


@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public CustomerResponse create(@RequestBody CustomerRequest request) {

        Customer customer = new Customer(
                request.fullName,
                request.email,
                request.phone
        );

        Customer saved = service.save(customer);

        CustomerResponse response = new CustomerResponse();
        response.id = saved.getId();
        response.fullName = saved.getFullName();
        response.email = saved.getEmail();
        response.phone = saved.getPhone();

        return response;
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.findAll();
    }

    @PutMapping("/{id}/iban")
    public void updateIban(
            @PathVariable Long id,
            @RequestBody UpdateIbanRequest request
    ) {
        Customer customer = service.findById(id);
        customer.setNlIban(request.getIban());
        service.save(customer);
    }


}
