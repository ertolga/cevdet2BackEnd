package com.tolga.customer.controller;

import com.tolga.customer.dto.LoginRequest;
import com.tolga.customer.dto.LoginResponse;
import com.tolga.customer.model.Customer;
import com.tolga.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CustomerService service;

    public AuthController(CustomerService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Customer customer = service.findByEmail(request.email);

        if (customer == null) {
            throw new RuntimeException("User not found");
        }

        if (!customer.getPassword().equals(request.password)) {
            throw new RuntimeException("Invalid password");
        }

        LoginResponse response = new LoginResponse();
        response.id = customer.getId();
        response.fullName = customer.getFullName();
        response.email = customer.getEmail();

        return response;
    }
}
