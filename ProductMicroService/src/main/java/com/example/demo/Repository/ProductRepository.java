package com.example.demo.Repository;

import com.example.demo.Repository.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("Select p from Product p where p.price < :price")
	List<Product> filterByPrice(@Param("price") Double price);

	@Query("Select p from Product p where p.name = :name")
	List<Product> filterByName(@Param("name") String name);

}
