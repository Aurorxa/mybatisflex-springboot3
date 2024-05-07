package com.github.web;

import com.github.dao.CustomerRepository;
import com.github.domain.Customer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping("/api/customers")
    List<Customer> getAll() {
        return customerRepository.findAll();
    }
}
