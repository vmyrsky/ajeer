package com.example.util;

import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.example.entity.Person;
import com.example.entity.PhoneNumber.Type;
import com.example.model.ConstraintViolationMessage;

/**
 * Tests that the errors returned by the BeanValidator are comprehensible.
 */
@RunWith(JUnit4.class)
public class BeanValidatorTest extends TestCase {

	private static BeanValidator validator = null;
	private static EJBContainer ec = null;
	private static Context ctx = null;

	@BeforeClass
	public static void init() {
		validator = new BeanValidator();
		System.out.println("Did you remember to start DB?");
	}

	// @AfterClass
	public static void closeContainer() {

		ec.close();
		try {
			ctx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		} finally {
			ec = null;
			ctx = null;
		}
	}

	@Test
	public void testOkPersonValidation() {
		Person person = new Person(true);
		List<ConstraintViolationMessage> errors = validator.validate(person);
		assertEquals(0, errors.size());
	}

	/**
	 * This will check that the person itself is valid.
	 */
	@Test
	public void testFailPersonValidation() {
		Person person = new Person(true);
		person.setLastName("Z");
		List<ConstraintViolationMessage> errors = validator.validate(person);
		assertEquals(1, errors.size());
		assertEquals("lastName", errors.get(0).getItemName());
		assertEquals("Z", errors.get(0).getErrorValue());
		assertEquals("size must be between 2 and 64", errors.get(0).getErrorMessage());
	}

	/**
	 * This will check that the validation is reached to phone numbers as well
	 */
	@Test
	public void testFailPhoneNumberValidation() {
		Person person = new Person(true);
		// Using null value should default to 'CELL' in actual container when the null value is handled by interceptor,
		// but Interceptors do not work when doing plain JUnit test
		person.getPhoneNumbers().get(0).setNumberType((Type) null);
		System.out.println(person.getPhoneNumbers().get(0).getNumberType());
		person.getPhoneNumbers().get(0).setPhoneNumber("123");
		List<ConstraintViolationMessage> errors = validator.validate(person);
		assertEquals(2, errors.size());

		// The order of validated items may change
		ConstraintViolationMessage error1, error2;
		if (errors.get(0).getItemName().equals("phoneNumbers[0].numberType")) {
			error1 = errors.get(0);
			error2 = errors.get(1);
		} else {
			error1 = errors.get(1);
			error2 = errors.get(0);
		}
		System.out.println(error1.toString());
		System.out.println(error2.toString());

		assertEquals("phoneNumbers[0].numberType", error1.getItemName());
		assertEquals("null", error1.getErrorValue());
		assertEquals("may not be null", error1.getErrorMessage());
		assertEquals("phoneNumbers[0].phoneNumber", error2.getItemName());
		assertEquals("123", error2.getErrorValue());
		assertEquals("size must be between 4 and 24", error2.getErrorMessage());
	}
}
