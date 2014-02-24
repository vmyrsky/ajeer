package com.example.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;

import com.example.model.ConstraintViolationMessage;

/**
 * Validates the given bean and gives proper validation error messages. (Adjust as needed)
 */
@Stateless
public class BeanValidator {

	/**
	 * Default constructor.
	 */
	public BeanValidator() {

	}

	/**
	 * Validates the person entity and returns a list of items not passing the validation.
	 * 
	 * @param person The person to validate
	 * @return A map of [&lt;item name&gt;, &lt;error text&gt;] errors. Will return empty map if there weren't any
	 *         errors.
	 */
	public List<ConstraintViolationMessage> validate(Person person) {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
		List<ConstraintViolationMessage> violationErrors = new ArrayList<>();
		for (ConstraintViolation<Person> violation : constraintViolations) {
			String invalidValue = "null";
			if (violation.getInvalidValue() != null) {
				invalidValue = violation.getInvalidValue().toString();
			}
			ConstraintViolationMessage msg = new ConstraintViolationMessage(violation.getPropertyPath().toString(),
			        invalidValue, violation.getMessage());
			violationErrors.add(msg);
		}
		return violationErrors;
	}

	/**
	 * Validates the phone number entity and returns a list of items not passing the validation.
	 * 
	 * @param person The person to validate
	 * @return A map of [&lt;item name&gt;, &lt;error text&gt;] errors. Will return empty map if there weren't any
	 *         errors.
	 */
	public List<ConstraintViolationMessage> validate(PhoneNumber number) {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<PhoneNumber>> constraintViolations = validator.validate(number);
		List<ConstraintViolationMessage> violationErrors = new ArrayList<>();
		for (ConstraintViolation<PhoneNumber> violation : constraintViolations) {
			String invalidValue = "null";
			if (violation.getInvalidValue() != null) {
				invalidValue = violation.getInvalidValue().toString();
			}
			ConstraintViolationMessage msg = new ConstraintViolationMessage(violation.getPropertyPath().toString(),
			        invalidValue, violation.getMessage());
			violationErrors.add(msg);
		}
		return violationErrors;
	}
}
