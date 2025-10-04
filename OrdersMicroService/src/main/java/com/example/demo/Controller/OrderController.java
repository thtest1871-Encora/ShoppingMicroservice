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

	private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrders(){
    	return orderService.getOrders();
    }
    
    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable("orderId") Long orderId){
    	return orderService.getOrderById(orderId);
    }
    
    @GetMapping("/customer/{customerId}")
    public List<Order> getOrderByCustomerId(@PathVariable("customerId") Long customerId){
    	return orderService.getOrderByCustomerId(customerId);
    }
    
    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }
    
    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable("orderId") Long orderId) {
    	return orderService.deleteOrderById(orderId);
    }
}
