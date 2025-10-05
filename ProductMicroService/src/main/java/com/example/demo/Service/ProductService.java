package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.Product;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.exception.InvalidQuantityException;
import com.example.demo.exception.ProductNotFoundException;

@Service
public class ProductService {

	// Automatically injects the ProductRepository dependency for database operations
		@Autowired
		private ProductRepository productRepository;

		// Retrieves and returns all products from the database
		public List<Product> getAllProducts() {
			return productRepository.findAll();
		}

		// Retrieves a product by its ID; throws an exception if not found
		public Optional<Product> getProductById(Long id) {
			Optional<Product> product = productRepository.findById(id);
			if(product.isEmpty()) {
				throw new ProductNotFoundException("Product Not Found with ID: " + id);
			}
			return product;
		}

		// Deletes a product by ID; throws an exception if the product does not exist
		public void deleteProduct(Long id) {
			Optional<Product> product = productRepository.findById(id);
			if(product.isEmpty()) {
				throw new ProductNotFoundException("Product Not Found with ID: " + id);
			}
			productRepository.deleteById(id);
		}

		// Adds a new product; validates that quantity is not negative before saving
		public Product addProduct(Product prod) {
			if (prod.getQuantity()  < 0) {
		        throw new InvalidQuantityException("Quantity cannot be negative for product ");
		    }
			return productRepository.save(prod);
		}

		// Retrieves a list of products whose prices are less than the specified value
		public List<Product> getProductByPriceLessThan(Double price) {
			return productRepository.filterByPrice(price);
		}

		// Retrieves a list of products matching the given name (case-insensitive or partial, based on repository logic)
		public List<Product> filterByName(String name) {
			return productRepository.filterByName(name);
		}
		
		// Updates a product’s quantity (either increase or decrease based on count value)
		public Product updateQuantity(Long id, int count) {
			// Fetch the product by ID or throw an exception if not found
			Product product = productRepository.findById(id)
	                						   .orElseThrow(
	                								   () -> new ProductNotFoundException("Product Not Found with ID: " + id));
			// Prevent negative quantity updates
			if (product.getQuantity() + count < 0) {
		        throw new InvalidQuantityException("Quantity cannot be negative for product ID: " + id);
		    }
	        // Update the quantity and save the changes
	        product.setQuantity(product.getQuantity() + count);
	        return productRepository.save(product);
		}
 
}
