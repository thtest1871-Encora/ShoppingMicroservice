	package com.example.demo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Repository.OrderRepository;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.Product;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.InvalidQuantityException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.model.Order;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
	
	@Autowired
	private OrderRepository orderRepository;
	
	public OrderService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public List<Order> getOrders() {
		return orderRepository.findAll();
	}
	
	public Order getOrderById(Long orderId){
		return orderRepository.findById(orderId)
							  .orElseThrow(
									  () -> new OrderNotFoundException("Order not found with ID: " + orderId)
									  );
	}
	
	public List<Order> getOrderByCustomerId(Long customerId){
		return orderRepository.getOrderByCustomerId(customerId);
	}
	
	public Order placeOrder(OrderRequest request) {
		CustomerDTO customer;
		try {
			customer = restTemplate.getForObject("http://CUSTOMERSERVICE/customers/" + request.getCustomerId(), CustomerDTO.class);
			System.out.println(customer.getId());
		}catch (RestClientException ex) {
			throw new CustomerNotFoundException("Customer Not Found with ID: " + request.getCustomerId());
		}
		
		Product product;
        try {
            product = restTemplate.getForObject(
                    "http://PRODUCTMICROSERVICE/products/" + request.getProductId(), Product.class);
        } catch (RestClientException ex) {
            throw new ProductNotFoundException("Unable to fetch product with ID: " + request.getProductId());
        }
		
        if (product == null) {
            throw new ProductNotFoundException("Product not found with ID: " + request.getProductId());
        }
        
        if(request.getQuantity() <= 0) {
        	throw new InvalidQuantityException(
        			"Invalid Quantity: Quantity should be greater than 0,"
        			+ " You have Requested: " + request.getQuantity()
        			);
        }
		
        if (product.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + request.getQuantity());
        }
		
		double totalPrice = product.getPrice() * request.getQuantity();
		
		Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);
        restTemplate.put(
                "http://PRODUCTMICROSERVICE/products/" + request.getProductId() +
                        "/decreaseQuantity?count=" + request.getQuantity(),
                null
        );
        return order;
	}
	
	public String deleteOrderById(Long orderId) {
		Order order = orderRepository.findById(orderId)
											   .orElseThrow(() -> new OrderNotFoundException("Order Not Found with ID: " + orderId));
		restTemplate.put(
				"http://PRODUCTMICROSERVICE/products/" + order.getProductId() + 
				"/increaseQuantity?count=" + order.getQuantity(),
				null
				);
		orderRepository.deleteById(orderId);
		return "Order by Customer " + order.getCustomerId() + " with Order ID : " + order.getId() + " has been Cancelled Successfully.\n"+
				"Quantity of " + order.getQuantity() + " has been added to the Stock\n" + 
				"Order Amount "+ order.getTotalPrice() +" will be refunded in 2 working Days";
	}
}
