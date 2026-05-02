package com.devnoir.blog.resources.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.DatabaseException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(status).body(new StandardError(Instant.now(), status.value(), "Resource not found", e.getMessage(), request.getRequestURI()));
	}
	
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(new StandardError(Instant.now(), status.value(), "Database exception", e.getMessage(), request.getRequestURI()));
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<StandardError> business(BusinessException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		return ResponseEntity.status(status).body(new StandardError(Instant.now(), status.value(), "Business rule violation", e.getMessage(), request.getRequestURI()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		ValidationError error = new ValidationError();
		error.setError("Validation exception");
		error.setTimestamp(Instant.now());
		error.setMessage(e.getMessage());
		error.setStatus(status.value());
		error.setPath(request.getRequestURI());
	
		for (FieldError f : e.getBindingResult().getFieldErrors()) {
			error.addError(f.getField(), f.getDefaultMessage());
		}
		return ResponseEntity.status(status).body(error);
	}
}