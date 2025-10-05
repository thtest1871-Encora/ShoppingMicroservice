package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.Product;
import com.example.demo.Service.ProductService;



@RestController
@RequestMapping("/products")
public class ProductController {

	
	@Autowired
	ProductService productService;
	
	// Fetches and returns all products from the database
		@GetMapping
		public List<Product> getProducts()
		{
			return productService.getAllProducts();
		}

		// Fetches a specific product by its ID using path variable
		@GetMapping("/{proId}")
		public Optional<Product> getProductById(@PathVariable("proId") long proId){
			return productService.getProductById(proId);
		}
		
		// Adds a new product to the database and returns the created product with HTTP 201 status
		@PostMapping
		public ResponseEntity<Product> addProduct(@RequestBody Product pro){
			Product savedProduct = productService.addProduct(pro);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(savedProduct);	
		}
		
		// Deletes a product by its ID and returns a success message
		@DeleteMapping("/{productId}")
		public String deleteProduct(@PathVariable("productId") Long productId) {
			productService.deleteProduct(productId);
			return "Deleted Successfully";
		}
		
		// Decreases the quantity of a product by a specified count
		@PutMapping("/{id}/decreaseQuantity")
		public Product decreaseQuantity(@PathVariable("id") Long id, @RequestParam("count") Integer count) {
			return productService.updateQuantity(id, -1 * count);
		}
		
		// Increases the quantity of a product by a specified count
		@PutMapping("/{id}/increaseQuantity")
		public Product increaseQuantity(@PathVariable("id") Long id, @RequestParam("count") Integer count) {
			return productService.updateQuantity(id, count);
		}
		
		// Filters and returns products that have a price less than the given value
		@GetMapping("/filter")
		public List<Product> getProductByPriceLessThan(@RequestParam("price") Double price)
		{
			return productService.getProductByPriceLessThan(price);
		}
		
		// Filters and returns products whose names match the given parameter
		@GetMapping("/filtername")
		public List<Product> getProductByName(@RequestParam("name") String name)
		{
			return productService.filterByName(name);
		}
}
