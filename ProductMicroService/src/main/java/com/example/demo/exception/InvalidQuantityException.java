package com.example.demo.exception;

public class InvalidQuantityException extends RuntimeException{

	public InvalidQuantityException(String message) {
		super(message);
	}
}
