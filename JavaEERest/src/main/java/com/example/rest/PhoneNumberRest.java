package com.example.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
import com.example.entity.PhoneNumber.Type;
import com.example.service.interfaces.PhoneBookService;
import com.example.model.ConstraintViolationMessage;
import com.example.model.KeyValuePair;
import com.example.model.Response;
import com.example.model.Response.Status;
import com.example.util.BeanValidator;
import com.example.util.IdConverter;
import com.example.exceptions.BadParameterException;

/**
 * This class will respond to request involved in handling information of a single phone number. The url path to this
 * REST API is:<br/>
 * <code>http://localhost:8080/jeer/rest/phonenumber</code>
 */
@Path("/phonenumber")
public class PhoneNumberRest {

	public static final String NUMBER_ID_PARAM = "numberId";
	public static final String TYPE_PARAM = "type";
	public static final String TYPE_KEY_PARAM = "key";
	public static final String NUMBER_PARAM = "phoneNumber";
	public static final String DESCRIPTION_PARAM = "description";

	@Inject
	private BeanValidator validator;
	@Inject
	private IdConverter idConverter;

	@Inject
	private PhoneBookService pbService;

	/**
	 * Default constructor.
	 */
	public PhoneNumberRest() {

	}

	/**
	 * Constructor for injection.
	 * 
	 * @param pbService An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public PhoneNumberRest(PhoneBookService pbService) {
		this.setPhoneBookService(pbService);
	}

	/**
	 * This will respond to POST request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Will create a new phone number for a person.
	 * 
	 * @param number A json string that is mapped to the specified entity (with &#64;XmlRootElement)
	 * @return A {@link Response} object stating how things went
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPhoneNumber(PhoneNumber number, @QueryParam(value = PersonRest.PERSON_ID_PARAM) final String pid) {

		Response response = new Response();
		System.out.println("addPhoneNumber: " + number.toString());
		int personId = 0;
		try {
			personId = new Integer(pid);
		} catch (NumberFormatException nfe) {
			String msg = nfe.getMessage();
			System.out.println(msg);
			response.setResponseStatus(Status.ERROR);
			response.addPayloadItem(new ConstraintViolationMessage(PersonRest.PERSON_ID_PARAM, pid, nfe.getMessage()));
			return response;
		}

		// Get the person to add this number for
		Person person = this.pbService.getPerson(personId);
		if (person == null) {
			// Lazy coding (should throw/show an error for the user)
			// This will create a new person to have the number information stored always even if a person is
			// not found
			person = new Person();
			this.pbService.addPerson(person);
		}
		// Add the number for person + add the owner for the number
		person.addNumber(number);
		number.setOwner(person);
		// Validate the object
		List<ConstraintViolationMessage> errors = this.validator.validate(number);

		if (errors.size() == 0) {
			this.pbService.addPhoneNumber(number);
		} else {
			response.setResponseStatus(Status.WARNING);
			response.setDescription("The data given for new phone number is not valid to be persisted");
			response.setPayload(errors);
		}

		return response;
	}

	/**
	 * This will respond to DELETE request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Get a single person with all necessary details if the person with the given id exists in the database. If the id
	 * is less than zero (&lt; 0), an empty person is returned simply providing the json structure used.
	 * 
	 * @param personId The id of the person to get. Use negative value to get empty structure
	 * @return A {@link Response} object, having the person as a 'payload'
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNumber(@QueryParam(value = NUMBER_ID_PARAM) final String numberId) {

		int id = -1;
		try {
			id = this.idConverter.resolveId(NUMBER_ID_PARAM, numberId);
		} catch (BadParameterException bpe) {
			System.out.println(bpe.getMessage());
			return bpe.getResponse();
		}
		this.pbService.deletePhoneNumber(id);
		return new Response(Status.OK, "Phonenumber with id '" + numberId + "' removed.");
	}

	/**
	 * This will respond to GET request with dataType : 'json' & "Content-Type" : "text/plain".<br/>
	 * @return A {@link Response} object stating how things went
	 */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNumberTypes() {

		System.out.println("getNumberTypes");
		Response response = new Response();
		for (Type type : PhoneNumber.Type.values()) {
			String same = type.toString().toUpperCase();
			response.addPayloadItem(new KeyValuePair(same, same));
		}
		return response;
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
