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

	@Autowired
	private ProductRepository productRepository;

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Optional<Product> getProductById(Long id) {
		Optional<Product> product = productRepository.findById(id);
		if(product.isEmpty()) {
			throw new ProductNotFoundException("Product Not Found with ID: " + id);
		}
		return product;

	}

	public void deleteProduct(Long id) {
		Optional<Product> product = productRepository.findById(id);
		if(product.isEmpty()) {
			throw new ProductNotFoundException("Product Not Found with ID: " + id);
		}
		productRepository.deleteById(id);

	}

	public Product addProduct(Product prod) {
		if (prod.getQuantity()  < 0) {
	        throw new InvalidQuantityException("Quantity cannot be negative for product ");
	    }
		return productRepository.save(prod);
	}

	public List<Product> getProductByPriceLessThan(Double price) {
		return productRepository.filterByPrice(price);
	}

	public List<Product> filterByName(String name) {
		return productRepository.filterByName(name);
	}
	
	public Product updateQuantity(Long id, int count) {
		Product product = productRepository.findById(id)
                						   .orElseThrow(
                								   () -> new ProductNotFoundException("Product Not Found with ID: " + id));
		if (product.getQuantity() + count < 0) {
	        throw new InvalidQuantityException("Quantity cannot be negative for product ID: " + id);
	    }
        product.setQuantity(product.getQuantity() + count);
        return productRepository.save(product);
	}
 
}
