package com.example.exceptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.example.model.Response;
import junit.framework.TestCase;

@RunWith(JUnit4.class)
public class BadParameterExceptionTest extends TestCase {

	@Test
	public void checkMessageBuilding() {
		BadParameterException exception = new BadParameterException("test", "value", "Message formatting");
		String expected = "The given parameter 'test' with value 'value' was bad. Reason: Message formatting";
		assertEquals(expected, exception.getMessage());
		Response response = exception.getResponse();
		assertEquals(Response.Status.ERROR, response.getResponseStatus());
		assertEquals(expected, response.getDescription());
	}
}
