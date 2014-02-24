package com.example.exceptions;

import com.example.model.Response;
import com.example.model.Response.Status;

public class BadParameterException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String msg = "The given parameter '%1$s' with value '%2$s' was bad. Reason: %3$s";
	private final Response response;

	public BadParameterException(String paramName, String value, String reason) {
		super(formatMessage(paramName, value, reason));
		this.response = new Response(Status.ERROR, super.getMessage());
	}

	private static String formatMessage(String paramName, String value, String reason) {
		String formattedMsg = String.format(msg, paramName, value, reason);
		return formattedMsg;
	}

	public Response getResponse() {
		return this.response;
	}
}
