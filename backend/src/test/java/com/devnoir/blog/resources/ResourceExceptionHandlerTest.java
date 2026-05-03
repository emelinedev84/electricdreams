package com.devnoir.blog.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.devnoir.blog.resources.exceptions.ResourceExceptionHandler;
import com.devnoir.blog.resources.exceptions.StandardError;
import com.devnoir.blog.resources.exceptions.ValidationError;
import com.devnoir.blog.services.exceptions.BusinessException;
import com.devnoir.blog.services.exceptions.DatabaseException;
import com.devnoir.blog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceExceptionHandlerTest {

	private final ResourceExceptionHandler handler = new ResourceExceptionHandler();

    @Test
    void entityNotFoundShouldReturn404StandardError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/PT/posts/missing");

        var response = handler.entityNotFound(
                new ResourceNotFoundException("Published content not found"),
                request);
        
        StandardError body = response.getBody();
        assertNotNull(body);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, body.getStatus());
        assertEquals("Resource not found", body.getError());
        assertEquals("Published content not found", body.getMessage());
        assertEquals("/api/PT/posts/missing", body.getPath());
    }

    @Test
    void databaseShouldReturn400StandardError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/categories/1");

        var response = handler.database(
                new DatabaseException("Integrity violation"),
                request);
        
        StandardError body = response.getBody();
        assertNotNull(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, body.getStatus());
        assertEquals("Database exception", body.getError());
        assertEquals("Integrity violation", body.getMessage());
    }

    @Test
    void businessShouldReturn422StandardError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/users/1/profile");

        var response = handler.business(
                new BusinessException("Email already exists"),
                request);
        
        StandardError body = response.getBody();
        assertNotNull(body);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(422, body.getStatus());
        assertEquals("Business rule violation", body.getError());
        assertEquals("Email already exists", body.getMessage());
    }

    @Test
    void validationShouldReturn422ValidationErrorWithFields() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/posts");

        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "postCreateDTO");
        bindingResult.addError(new FieldError("postCreateDTO", "categories", "At least one category is required"));

        MethodParameter parameter = new MethodParameter(
                ResourceExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", Object.class),
                0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.validation(exception, request);
        
        ValidationError body = response.getBody();
        assertNotNull(body);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(422, body.getStatus());
        assertEquals("Validation exception", body.getError());
        assertEquals("/api/admin/posts", body.getPath());
        assertEquals(1, body.getErrors().size());
        assertEquals("categories", body.getErrors().get(0).getFieldName());
        assertEquals("At least one category is required", body.getErrors().get(0).getMessage());
    }

    @SuppressWarnings("unused")
    private void dummyMethod(Object value) {
    }
}
