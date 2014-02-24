package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
/**
 * This class will simply hold the response status for other that 'GET' requests.
 */
public class Response implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlEnum
	public enum Status {
		OK, ERROR, WARNING
	};

	private Status responseStatus = null;
	private String description = null;
	/**
	 * An entity will be provided as a payload of this response
	 */
	
	// JAXB: Wraps the payload elements in an element named as 'payload' 
	@XmlElementWrapper(name = "payload")
	// JAXB: Will specify what encapsulation is used for each payload entity (Mandatory to have the entities specified)
	// Note: There may a bug or I may not have understood it correctly,
		// but specifying the 'name' for elements list does not work
	@XmlElementRefs({@XmlElementRef(type = KeyValuePair.class), @XmlElementRef(type = Person.class), @XmlElementRef(type = PhoneNumber.class)})
	private List<Object> payload = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public Response() {
		this.responseStatus = Status.OK;
		this.description = "";
	}

	public Response(Status responseStatus, String errorDescription) {
		this.responseStatus = responseStatus;
		this.description = errorDescription;
	}

	public void setResponseStatus(Status responseStatus) {
		this.responseStatus = responseStatus;
	}

	public Status getResponseStatus() {
		return responseStatus;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Returns the entity that has been set as payload with this response.
	 * 
	 * @return The payload
	 */
	public List<Object> getPayload() {
		return payload;
	}

	/**
	 * Add any entity that should be sent as a payload with this response.
	 * 
	 * @param payload The entity to have as payload
	 */
	public void addPayloadItem(Object payloadItem) {
		this.payload.add(payloadItem);
	}

	/**
	 * This will help to add a list of typed (must be annotated to xml) items as the payload.
	 * 
	 * @param payload A list of item to be added as the payload. The items must be annotated for xml.
	 */
	public void setPayload(List<?> payload) {
		for (Object payloadItem : payload) {
			this.addPayloadItem(payloadItem);
		}
	}

	/**
	 * A helper method to clear the payload if clearing is needed for some reason.
	 */
	public void clearPayload() {
		this.payload = new ArrayList<>();
	}
}
