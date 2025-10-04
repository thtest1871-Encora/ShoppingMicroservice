package com.example.customers.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.customers.dto.CustomerOrderResponse;
import com.example.customers.dto.Order;
import com.example.customers.exception.CustomerNotFoundException;
import com.example.customers.model.Customer;
import com.example.customers.repository.CustomerRepository;

@Service
public class CustomerService {
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public Optional<Customer> getCustomerById(Long id){
		Optional<Customer> customer = customerRepository.findById(id);
		if(customer.isEmpty()) {
			throw new CustomerNotFoundException("Customer Not Found with ID: " + id);
		}
		return customer;
	}
	
	public CustomerOrderResponse getOrdersByCustomers(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
											  .orElseThrow(
													  () -> new CustomerNotFoundException("Customer Not Found with ID: " + customerId)
												);
		Order[] orders = restTemplate.getForObject(
				"http://ORDERSMICROSERVICE/orders/customer/" + customerId,
				Order[].class);
		List<Order> orderList = (orders != null) ? Arrays.asList(orders) : List.of();
		CustomerOrderResponse response = new CustomerOrderResponse();
        response.setCustomer(customer);
        response.setOrders(orderList);
        return response;
	}
	
}