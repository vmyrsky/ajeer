package com.example.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

//JAXB: This entity is the xml root and can be converted into xml/json to be sent for the Angular
@XmlRootElement(name = "phonenumber")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "\"phonenumber\"")
@NamedQueries({
        @NamedQuery(name = PhoneNumber.FIND_ALL, query = "SELECT pn FROM PhoneNumber pn"),
        @NamedQuery(name = PhoneNumber.FIND_BY_NUMBER, query = "SELECT pn FROM PhoneNumber pn where pn.phoneNumber=:number"
                + Person.PARAM_LASTNAME) })
public class PhoneNumber implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	public static final String FIND_ALL = "PhoneNumber.find_all";
	@Transient
	public static final String FIND_BY_NUMBER = "PhoneNumber.find_by_number";
	
	// JAXB: Enumerations need to be annotated
	@XmlEnum
	public static enum Type {
		HOME, WORK, CELL
	};

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "\"number_id\"")
	private int id;

	@Version
	@Column(name = "\"timestamp\"")
	private Timestamp timestamp = null;

	/**
	 * The owner of this phoneNumber
	 */
	// Bean Validation to have check that there is always owner for the number
	@NotNull
	// JAXB: Prevent from getting into loop when generating the json (when the opposing side already maps these into
	// xml)
	@XmlTransient
	// JPA: The owning side is the 'many' side
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "\"person_id\"")
	private Person owner = null;

	/**
	 * The type of the phone number.
	 */
	// JPA: Informs that the enumerated types defined above must be stored as Strings
	// into the database
	@Enumerated(EnumType.STRING)
	@Column(name = "\"type\"")
	@NotNull
	private Type numberType = null;

	@Column(name = "\"phonenumber\"")
	@NotNull
	@Size(min = 4, max = 24)
	// There could be all sorts of regexp / listener validation to check that the number is in correct format / valid
	// number (we leave this fancy checking out now)
	private String phoneNumber = null;

	// JPA: Tells that the field may be left 'null' (this is allowed by default, but 'optional' is here for
	// documentation)
	@Basic(optional = true)
	@Column(name = "\"description\"")
	@Size(max = 256)
	private String description = null;

	/**
	 * Default constructor.
	 */
	public PhoneNumber() {

	}

	public PhoneNumber(boolean populateDefault) {
		if (populateDefault) {
			this.setOwner(null);
			this.setNumberType(Type.CELL);
			this.setPhoneNumber("5551234567");
			this.setDescription("Default number");
		}
	}

	/**
	 * Constructor for fast creation.
	 */
	public PhoneNumber(Person owner, Type numberType, String phoneNumber, String description) {
		this.setOwner(owner);
		this.setNumberType(numberType);
		this.setPhoneNumber(phoneNumber);
		this.setDescription(description);
	}

	/**
	 * Constructor for testing.
	 */
	public PhoneNumber(int id, Person owner, Type numberType, String phoneNumber, String description) {
		this.id = id;
		this.setOwner(owner);
		this.setNumberType(numberType);
		this.setPhoneNumber(phoneNumber);
		this.setDescription(description);
	}

	@AroundInvoke
	private Object interceptPhoneNumberType(InvocationContext ctx) {
		Object[] params = ctx.getParameters();
		// params[0] == numberType
		if (params[0] == null) {
			params[0] = Type.CELL;
			System.out.println("The phonen umber type was 'null'. Defaulted to 'CELL'");
		}
		ctx.setParameters(params);
		return ctx;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public Type getNumberType() {
		return numberType;
	}

	public void setNumberType(String numberType) {
		Type type = Type.valueOf(numberType);
		this.setNumberType(type);
	}

	public void setNumberType(Type numberType) {
		this.numberType = numberType;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	// The interceptor is defined in this class
	@Interceptors(PhoneNumber.class)
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("PhoneNumber: [").append(this.getId()).append(", ");
		sb.append(this.getNumberType()).append(", ");
		sb.append(this.getPhoneNumber()).append(", ");
		sb.append(this.getDescription()).append("]");
		return sb.toString();
	}
}
