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
	
	@GetMapping("/test")
	public String test() {
		return "testing";
	}
	
	@GetMapping
	public List<Product> getProducts()
	{
		return productService.getAllProducts();
		
	}

	@GetMapping("/{proId}")
	public Optional<Product> getProductById(@PathVariable("proId") long proId){
		return productService.getProductById(proId);
	}
	
	@PostMapping
	public ResponseEntity<Product> addProduct(@RequestBody Product pro){
		Product savedProduct = productService.addProduct(pro);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(savedProduct);	
	}
	
	@DeleteMapping("/{productId}")
	public String deleteProduct(@PathVariable("productId") Long productId) {
		productService.deleteProduct(productId);
		return "Deleted Successfully";
	}
	
	@PutMapping("/{id}/decreaseQuantity")
	public Product decreaseQuantity(@PathVariable("id") Long id, @RequestParam("count") Integer count) {
		return productService.updateQuantity(id, -1 * count);
	}
	
	@PutMapping("/{id}/increaseQuantity")
	public Product increaseQuantity(@PathVariable("id") Long id, @RequestParam("count") Integer count) {
		return productService.updateQuantity(id, count);
	}
	
	
	@GetMapping("/filter")
	public List<Product> getProductByPriceLessThan(@RequestParam("price") Double price)
	{
		return productService.getProductByPriceLessThan(price);
	}
	
	
	@GetMapping("/filtername")
	public List<Product> getProductByName(@RequestParam("name") String name)
	{
		return productService.filterByName(name);
	}
}
