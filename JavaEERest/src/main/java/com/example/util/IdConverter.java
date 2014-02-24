package com.example.util;

import javax.ejb.Stateless;
import com.example.exceptions.BadParameterException;

@Stateless
public class IdConverter {

	public IdConverter() {

	}

	/**
	 * Checks that the parameter value can be converted to integer. Throws an exception that can be show in UI if the
	 * value is not ok.
	 * 
	 * @param personId The id value to be converted.
	 * @return The integer value of the given param.
	 * @throws BadParameterException When the value can not be converted to integer. Will contain the response object to
	 *         send to UI.
	 */
	public int resolveId(String paramName, String value) throws BadParameterException {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			String msg = "The given value is not an integer";
			throw new BadParameterException(paramName, value, msg);
		}
	}
}
