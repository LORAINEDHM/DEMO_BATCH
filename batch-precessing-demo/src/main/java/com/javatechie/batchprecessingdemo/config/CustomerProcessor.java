package com.javatechie.batchprecessingdemo.config;

import com.javatechie.batchprecessingdemo.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        return customer;
    }  // <Input, Output>
}
