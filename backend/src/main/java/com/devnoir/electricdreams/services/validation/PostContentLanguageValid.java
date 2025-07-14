package com.devnoir.electricdreams.services.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PostContentLanguageValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PostContentLanguageValid {

	String message() default "At least one language content must be provided";
	 
 	Class<?>[] groups() default {};
 
 	Class<? extends Payload>[] payload() default {};
}
