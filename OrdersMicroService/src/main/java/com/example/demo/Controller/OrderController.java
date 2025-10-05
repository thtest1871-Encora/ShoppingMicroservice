package com.example.demo.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.OrderService;
import com.example.demo.dto.OrderRequest;
import com.example.demo.model.Order;

@RestController
@RequestMapping("/orders")
public class OrderController {

	// Injects the OrderService dependency using constructor injection (recommended for immutability and testing)
		private final OrderService orderService;

	    // Constructor for initializing OrderService
	    public OrderController(OrderService orderService) {
	        this.orderService = orderService;
	    }

	    // Retrieves and returns a list of all orders
	    @GetMapping
	    public List<Order> getOrders(){
	    	return orderService.getOrders();
	    }
	    
	    // Retrieves a specific order by its ID
	    @GetMapping("/{orderId}")
	    public Order getOrderById(@PathVariable("orderId") Long orderId){
	    	return orderService.getOrderById(orderId);
	    }
	    
	    // Retrieves all orders placed by a specific customer using their customer ID
	    @GetMapping("/customer/{customerId}")
	    public List<Order> getOrderByCustomerId(@PathVariable("customerId") Long customerId){
	    	return orderService.getOrderByCustomerId(customerId);
	    }
	    
	    // Places a new order using the details provided in the request body (OrderRequest DTO)
	    @PostMapping
	    public Order placeOrder(@RequestBody OrderRequest request) {
	        return orderService.placeOrder(request);
	    }
	    
	    // Deletes an existing order by its ID and returns a confirmation message
	    @DeleteMapping("/{orderId}")
	    public String deleteOrder(@PathVariable("orderId") Long orderId) {
	    	return orderService.deleteOrderById(orderId);
	    }
}
