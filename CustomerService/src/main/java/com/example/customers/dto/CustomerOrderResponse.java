package com.example.customers.dto;

import java.util.List;

import com.example.customers.model.Customer;

public class CustomerOrderResponse {
	private Customer customer;
    private List<Order> orders;

    public Customer getCustomer() { 
    	return customer; 
    }
    public void setCustomer(Customer customer) { 
    	this.customer = customer; 
    }

    public List<Order> getOrders() { 
    	return orders; 
    }
    public void setOrders(List<Order> orders) {
    	this.orders = orders;
    }
}
