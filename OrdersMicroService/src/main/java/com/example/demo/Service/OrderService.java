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

	// Used to make REST API calls to other microservices (Customer and Product services)
    private final RestTemplate restTemplate;
	
    // Injects the repository dependency for database interactions
	@Autowired
	private OrderRepository orderRepository;
	
	// Constructor injection for RestTemplate dependency
	public OrderService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	// Retrieves and returns all orders from the database
	public List<Order> getOrders() {
		return orderRepository.findAll();
	}
	
	// Retrieves a specific order by ID; throws an exception if not found
	public Order getOrderById(Long orderId){
		return orderRepository.findById(orderId)
							  .orElseThrow(
									  () -> new OrderNotFoundException("Order not found with ID: " + orderId)
									  );
	}
	
	// Retrieves all orders placed by a specific customer using their ID
	public List<Order> getOrderByCustomerId(Long customerId){
		return orderRepository.getOrderByCustomerId(customerId);
	}
	
	// Places a new order by validating customer, product, and stock availability
	public Order placeOrder(OrderRequest request) {
		CustomerDTO customer;
		try {
			// Calls Customer Microservice to verify the existence of the customer
			customer = restTemplate.getForObject("http://CUSTOMERSERVICE/customers/" + request.getCustomerId(), CustomerDTO.class);
			System.out.println(customer.getId());
		}catch (RestClientException ex) {
			// Throws exception if customer not found or service is unavailable
			throw new CustomerNotFoundException("Customer Not Found with ID: " + request.getCustomerId());
		}
		
		Product product;
        try {
        	// Calls Product Microservice to fetch product details by product ID
            product = restTemplate.getForObject(
                    "http://PRODUCTMICROSERVICE/products/" + request.getProductId(), Product.class);
        } catch (RestClientException ex) {
        	// Throws exception if product not found or service unavailable
            throw new ProductNotFoundException("Unable to fetch product with ID: " + request.getProductId());
        }
		
        // Handles case where product object is null (not found)
        if (product == null) {
            throw new ProductNotFoundException("Product not found with ID: " + request.getProductId());
        }
        
        // Validates that requested quantity is positive
        if(request.getQuantity() <= 0) {
        	throw new InvalidQuantityException(
        			"Invalid Quantity: Quantity should be greater than 0,"
        			+ " You have Requested: " + request.getQuantity()
        			);
        }
		
        // Ensures product stock is sufficient for the order
        if (product.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + request.getQuantity());
        }
		
        // Calculates the total price based on product price and ordered quantity
		double totalPrice = product.getPrice() * request.getQuantity();
		
		// Creates a new order object and sets its properties
		Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);

        // Saves the new order in the database
        orderRepository.save(order);
        
        // Sends a PUT request to Product Microservice to decrease product stock after successful order
        restTemplate.put(
                "http://PRODUCTMICROSERVICE/products/" + request.getProductId() +
                        "/decreaseQuantity?count=" + request.getQuantity(),
                null
        );
        return order;
	}
	
	// Deletes an order by its ID and restores the product quantity in the Product Microservice
	public String deleteOrderById(Long orderId) {
		// Fetch order or throw exception if not found
		Order order = orderRepository.findById(orderId)
											   .orElseThrow(() -> new OrderNotFoundException("Order Not Found with ID: " + orderId));
		
		// Sends a PUT request to Product Microservice to increase the product quantity back in stock
		restTemplate.put(
				"http://PRODUCTMICROSERVICE/products/" + order.getProductId() + 
				"/increaseQuantity?count=" + order.getQuantity(),
				null
				);
		
		// Deletes the order record from the database
		orderRepository.deleteById(orderId);
		
		// Returns a detailed confirmation message after successful cancellation
		return "Order by Customer " + order.getCustomerId() + " with Order ID : " + order.getId() + " has been Cancelled Successfully.\n"+
				"Quantity of " + order.getQuantity() + " has been added to the Stock\n" + 
				"Order Amount "+ order.getTotalPrice() +" will be refunded in 2 working Days";
	}
}
