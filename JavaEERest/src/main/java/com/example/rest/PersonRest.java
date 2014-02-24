package com.example.rest;

import java.util.List;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
import com.example.service.interfaces.PhoneBookService;
import com.example.model.ConstraintViolationMessage;
import com.example.model.Response;
import com.example.model.Response.Status;
import com.example.util.BeanValidator;
import com.example.util.IdConverter;
import com.example.exceptions.BadParameterException;

/**
 * This class will respond to request involved in handling information of a single person or multiple phone numbers
 * (numbers are child elements of the person). The url path to this REST API is:<br/>
 * <code>http://localhost:8080/jeer/rest/person</code><br/>
 * To handle operations per phone number, use {@link PhoneNumberRest}.
 */
// http://localhost:8080/jeer/rest/phonebook
@Path("/person")
public class PersonRest {

	public static final String PERSON_ID_PARAM = "personId";
	public static final String NAMES_PARAM = "names";
	public static final String LAST_NAME_PARAM = "lastName";

	@Inject
	private BeanValidator validator;
	@Inject
	private IdConverter idConverter;

	// CDI: Container manages the injection. Have get/set methods for this if it is private
	@Inject
	private PhoneBookService pbService;

	/**
	 * Default constructor.
	 */
	public PersonRest() {

	}

	/**
	 * Constructor for injection.
	 * 
	 * @param pbService An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public PersonRest(PhoneBookService pbService) {
		this.setPhoneBookService(pbService);
	}

	/**
	 * This will respond to GET request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Gets a single person with all necessary details if the person with the given id exists in the database. If the id
	 * is less than zero (&lt; 0), an empty person is returned simply providing the json structure used.
	 * 
	 * @param personId The id of the person to get. Use negative value to get empty structure
	 * @return A {@link Response} object, having the person data as a 'payload'
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPerson(@QueryParam(value = PERSON_ID_PARAM) final String personId,
	        @QueryParam(value = "phoneNumbers") final String phoneNumbers) {
		System.out.println("getPerson called");
		boolean includePhoneNumbers = new Boolean(phoneNumbers);
		int id = -1;
		try {
			id = this.idConverter.resolveId(PERSON_ID_PARAM, personId);
		} catch (BadParameterException bpe) {
			System.out.println(bpe.getMessage());
			return bpe.getResponse();
		}

		if (id < 0) {
			Response response = new Response();
			Person person = new Person(true);
			response.addPayloadItem(person);
			if (includePhoneNumbers) {
				response.addPayloadItem(new PhoneNumber(true));
			}
			return response;
		} else {
			Response response = new Response();

			if (includePhoneNumbers) {
				response.setPayload(this.pbService.getPerson(id).getPhoneNumbers());
			}
			// Note, above is used 'set' which will replace all content
			response.addPayloadItem(this.pbService.getPerson(id));
			return response;
		}
	}

	/**
	 * This will respond to POST request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Will create a new person in database.
	 * 
	 * @param obj A json object containing all the information needed to create a new person. The structure should be
	 *        equal received with 'getPerson'. <b>Note: You do not have to (or should) use json to parse the values from
	 *        and to populate the entity. See {@link PhoneNumberRest#addPhoneNumber(PhoneNumber, String)} how you really
	 *        should do this</b>
	 * @return A {@link Response} object stating how things went
	 * @see #getPerson(String)
	 * @see PhoneNumberRest#addPhoneNumber(PhoneNumber, String)
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPerson(JsonObject obj) {
		System.out.println("addPerson: " + obj.toString());
		Response response = new Response();
		try {
			// Directly mapping the json to an entity can be done automatically, but this is example on how to receive
			// arbitrary json content
			// See: PhoneNumberRest.addPhoneNumber for better example
			Person newPerson = new Person();
			String names = obj.getString(NAMES_PARAM);
			newPerson.setNames(names);
			newPerson.setLastName(obj.getString(LAST_NAME_PARAM));
			// To create a mock number
			// newPerson.addNumber(new PhoneNumber(newPerson, Type.CELL, "1234567890", "test"));
			// Validate the object
			List<ConstraintViolationMessage> errors = this.validator.validate(newPerson);
			if (errors.size() == 0) {
				this.pbService.addPerson(newPerson);
			} else {
				response.setResponseStatus(Status.WARNING);
				response.setDescription("The data given is not valid to be persisted");
				response.setPayload(errors);
			}
		} catch (NumberFormatException nfe) {
			String msg = "Can not map the data into entity";
			System.out.println(msg);
			return new Response(Response.Status.ERROR, msg);
		}
		return response;
	}

	/**
	 * This will respond to DELETE request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Finds a single person with the given id and if found, removes it from the database.
	 * 
	 * @param personId The id of the person to remove
	 * @return A {@link Response} object stating how things went
	 * @see Response
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePerson(@QueryParam(value = PERSON_ID_PARAM) final String personId) {
		System.out.println("deletePerson called: " + personId);
		int id = -1;
		try {
			id = this.idConverter.resolveId(PERSON_ID_PARAM, personId);
		} catch (BadParameterException bpe) {
			System.out.println(bpe.getMessage());
			return bpe.getResponse();
		}
		this.pbService.deletePerson(id);
		return new Response(Status.OK, "Person with id '" + personId + "' removed.");
	}

	/**
	 * A method used with injection.
	 * 
	 * @return An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public PhoneBookService getPhoneBookService() {
		return pbService;
	}

	/**
	 * A method used with injection.
	 * 
	 * @param pbService An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public void setPhoneBookService(PhoneBookService pbService) {
		this.pbService = pbService;
	}
}
