package com.devnoir.electricdreams.services.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = TagValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TagValid {

	String message() default "Post content validation error";
	 
 	Class<?>[] groups() default {};
 
 	Class<? extends Payload>[] payload() default {};
}
