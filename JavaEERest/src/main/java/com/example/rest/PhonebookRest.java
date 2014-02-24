package com.example.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlTransient;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
import com.example.service.interfaces.PhoneBookService;
import com.example.model.ModelBean;
import com.example.model.Response;
import com.example.model.Response.Status;
import com.example.util.IdConverter;

/**
 * This class works as the main REST API to handle request to work with 'bulk Persons data'. (Also Contains example
 * methods how to interact with the REST API and operations for different data types). The 'URL' is specified further in
 * 'web.xml' if needed. The full url path will be (for example):<i>
 * &lt;protocol&gt;://&lt;host&gt;/&lt;war_name_in_pom.xml
 * &gt;/&lt;rest_path_in_web.xml&gt;/&lt;@Path_in_this_class&gt;</i><br/>
 * The url path to this REST API is:<br/>
 * <code>http://localhost:8080/jeer/rest/phonebook</code><br/>
 * To handle operations per person, use {@link PersonRest}.
 */
// Plain old Java Object: it does not extend a class or implement an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests by default to the HTML MIME type.

// Jersey: Sets the path to base [URL] + /rest
@Path("/phonebook")
public class PhonebookRest {

	// CDI: Container manages the injection. Have get/set methods for this if it is private
	@Inject
	private PhoneBookService pbService;
	@Inject
	private IdConverter idConverter;

	/**
	 * Default constructor.
	 */
	public PhonebookRest() {

	}

	/**
	 * Constructor for injection.
	 * @param pbService An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public PhonebookRest(PhoneBookService pbService) {
		this.setPbService(pbService);
	}

	/**
	 * This will respond to GET request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Will get a listing of all persons in the database
	 * 
	 * @param value An url parameter (<code>?param=xxx</code>) that will be mapped to method parameter (you need to
	 *        provide the annotation <code>@QueryParam(value = "param")</code> to pick this up)
	 * @return A {@link Response} object stating how things went
	 * @see PersonRest#getPerson(String, String)
	 * @see PersonRest#addPerson(JsonObject)
	 */
	// Jersey: This method is called if content-type (produces) is 'application/json'
	@GET
	// Jersey: This method is called if _also_ the 'dataType' (see the angular $http) is 'json'
	@Consumes(MediaType.APPLICATION_JSON)
	// Jersey: This method is called if 'content-type' (see the angular $http) is 'application/json'
	// @Produces({"application/json"}) == @Produces(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// Jersey: @QueryParam => define the url-param to be mapped onto method param
	public Response getAllPersons(@QueryParam(value = "param") final String value) {
		System.out.println("getAllPersons called with param: " + value);
		Response response = new Response();

		List<Person> persons = this.pbService.getAllPersons();

		if (persons.size() > 0) {
			response.setPayload(persons);
		} else {
			// Will create two dummy entities for example when you don't have a pre populated DB
			Person person = new Person();
			List<String> names = new ArrayList<>();
			names.add("Harry");
			names.add("'The mage'");
			person.setNames(names);
			person.setLastName("Potter");
			person.addNumber(new PhoneNumber(0, person, PhoneNumber.Type.CELL, "555-9999999",
			        "Harry's emergency number"));
			person.addNumber(new PhoneNumber(1, person, PhoneNumber.Type.WORK, "555-8888888", "Harry's secretary"));
			response.addPayloadItem(person);
			person = new Person();
			person.addName("Peter");
			names = new ArrayList<>();
			names.add("Peter");
			names.add("'Spidey'");
			person.setNames(names);
			person.setLastName("Parker");
			person.addNumber(new PhoneNumber(0, person, PhoneNumber.Type.HOME, "555-5555555", "Aunt May"));
			response.addPayloadItem(person);
		}
		return response;
	}

	/**
	 * This will respond to PUT request with dataType : 'json' & "Content-Type" : "application/json".<br/>
	 * Will update the provided list of person entities in database.
	 * 
	 * @param arr A json array containing all the information needed to update a list of persons. You may have any kind
	 *        of structure when you iterate the content manually, but it is easier if you have the same structure that
	 *        the serialized entity will have. The structure for each object should be equal to what got with
	 *        'getPerson'
	 * @return A {@link Response} object stating how things went
	 * @see PersonRest#getPerson(String, String)
	 * @see PersonRest#addPerson(JsonObject)
	 */
	// Note: You could have the list mapped directly to entities, but you may risk of losing data unintended if there
	// are for example @XmlTransient mappings in the entity (particular data not sent to UI at all => value will be
	// "null" when returned / mapped back to entity)
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// Note: You have to pay attention on the type of the data being sent.
	// e.g. The parameter type can be JsonObject or JsonArray
	// See PersonRest.addPerson for alternative
	public Response updatePersons(JsonArray arr) {
		System.out.println("updatePersons: " + arr.toString());
		Response response = new Response();

		try {
			List<Person> personsToUpdate = new ArrayList<>();
			for (int i = 0; i < arr.size(); i++) {
				JsonObject jsonPerson = arr.getJsonObject(i);
				int id = jsonPerson.getInt("id");
				// There is only two kinds of data we are interested in updating
				// The list of names and the last name
				Person person = this.pbService.getPerson(id);
				String names = jsonPerson.getString(PersonRest.NAMES_PARAM);
				person.setNames(names);
				person.setLastName(jsonPerson.getString(PersonRest.LAST_NAME_PARAM));
				personsToUpdate.add(person);
			}
			this.pbService.updatePersons(personsToUpdate);
		} catch (JsonException je) {
			System.out.println(je.getMessage());
			return new Response(Status.ERROR, je.getMessage());
		}
		return response;
	}

	/**
	 * This will respond to GET request with dataType : 'json' & "Content-Type" : "text/plain".
	 * 
	 * @return A json string presentation of the {@link ModelBean}
	 */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public ModelBean sayJSONHello() {
		System.out.println("sayJSONHello called");
		return new ModelBean(0, "JSON", "JSON response for: dataType: 'json', contentType : 'text/plain'");
	}

	/**
	 * This should (sorry, not tested) respond to GET request with dataType : 'xml' & "Content-Type" : "text/plain". You
	 * could use this to create a (xml) web-service interface method.
	 * 
	 * @return An xml string presentation of the {@link ModelBean}
	 */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_XML)
	public ModelBean sayXMLHello() {
		System.out.println("sayXMLHello called");
		return new ModelBean(2, "XML", "XML response");
	}

	/**
	 * This method is called by default (for example when accessing the url 'manually' from browser:
	 * http://localhost:8080/jeer/rest/phonebook?param=value) <br/>
	 * This will respond to GET request with dataType: 'text/html' & "Content-Type" : "text/html".
	 * 
	 * @return A html string which will be rendered by the browser
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		System.out.println("sayHtmlHello called");
		return "<html><title>REST says hello</title><body><h1>Hello</body></h1></html>";
	}

	/**
	 * This will respond to GET request with dataType : 'text' & "Content-Type" : "text/plain".
	 * 
	 * @return A plain string of your choice
	 */
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainHello() {

		System.out.println("sayPlainHello called");
		return "hello";
	}

	/**
	 * A method used with injection.
	 * 
	 * @return An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public PhoneBookService getPbService() {
		return pbService;
	}

	/**
	 * A method used with injection.
	 * 
	 * @param pbService An instance of {@link PhoneBookService} (interface in JPA2 project)
	 */
	public void setPbService(PhoneBookService pbService) {
		this.pbService = pbService;
	}

	/**
	 * A method used with injection.
	 * 
	 * @return The {@link IdConverter}
	 */
	public IdConverter getIdConverter() {
		return idConverter;
	}

	/**
	 * A method used with injection.
	 * 
	 * @param idConverter The {@link IdConverter}
	 */
	public void setIdConverter(IdConverter idConverter) {
		this.idConverter = idConverter;
	}
}
