package com.github.web;

import com.github.dao.CustomerRepository;
import com.github.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping("/api/customers")
    List<Customer> getAll() {
        return customerRepository.findAll();
    }
}
