package com.example.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;

import com.example.entity.PhoneNumber.Type;

/**
 * The persistence/entity class for the "person" database table.
 */
// JAXB: This entity is the xml root and can be converted into xml/json to be sent for the Angular
@XmlRootElement(name = "person")
// JAXB: FIELD is set every non static, non transient field will be automatically bound. PROPERTY instructs JAXB to do
// this for getter and setter pairs
@XmlAccessorType(XmlAccessType.FIELD)
// JPA: Defines this is a persistent class
@Entity
// Note: Have quoted table name to have it case sensitive
@Table(name = "\"person\"")
// JPA: Specify if to have the annotations on get-methods or on properties (FIELD =
// properties)
@Access(AccessType.FIELD)
// JPA: We have 'DISABLE_SELECTIVE' in persistence.xml, but it is good to have the intended cache state documented
@Cacheable(true)
// JPA: JPQL which will be used to get all persons persisted in the db The named JPQL's should be defined in the
// corresponding entity
// Note: The attribute name must match the property name of the entity, not the db-column name
@NamedQueries({
        @NamedQuery(name = Person.FIND_ALL, query = "SELECT p FROM Person p"),
        @NamedQuery(name = Person.FIND_BY_LASTNAME, query = "SELECT p FROM Person p where p.lastName=:"
                + Person.PARAM_LASTNAME) })
public class Person implements Serializable {

	private static final long serialVersionUID = 1L;

	// Use constants to point attributes for JPQL. Avoid using plain Strings => You have a lot of broken code if
	// refactoring and these errors won't show until runtime
	// JPA: This information will not be persisted
	@Transient
	public static final String FIND_ALL = "Person.find_all";
	@Transient
	public static final String FIND_BY_LASTNAME = "Person.find_by_lastname";
	@Transient
	public static final String PARAM_LASTNAME = "lastname";

	/**
	 * A unique id assigned for each person
	 */
	// Will create a unique id when the entity is persisted.
	@Id
	// JPA: There will be no setter method for the id since the value is generated automatically when the entity is
	// persisted (it will be unique value)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "\"person_id\"")
	private int id;

	// JPA: The version annotation and a timestamp field is needed for locking
	@Version
	@Column(name = "\"timestamp\"")
	private Timestamp timestamp = null;

	/**
	 * Just a list of names the person may have. These will be defined automatically in their own table, joined by the
	 * PK of this class
	 */
	// JAXB: Have this/these item(s) as element(s)
	@XmlElement
	// JAXB: Will print all items as a whitespace-separated String (Note: there is a missing feature that you cannot
	// specify the separator character yet)
	@XmlList
	// Bean validation stating the min and max constraints for the list size
	@Size(min = 1, max = 6)
	// BeanValidation instruction to validate all the list items
	// @Valid
	// Validating the String items in a list would be Java 8 specific feature (we are leaving it out currently)
	// private List<@Size(min=2, max=32) String> names;
	// JPA: Tells this is a simple list => A table will be generated for this, using the primary key for union
	@ElementCollection(fetch = FetchType.EAGER)
	// JPA: Define a name for the table that is generated to hold these names
	@CollectionTable(name = "\"person_names\"", joinColumns = @JoinColumn(name = "\"person_id\""))
	@Column(name = "\"name\"")
	private List<String> names;

	/**
	 * There can be only one last name, so it may be simple text String
	 */
	// Bean validation stating that this item can't be left null
	@NotNull
	// Bean validation stating the min and max constraints for the text
	@Size(min = 2, max = 64)
	// JPA: We want the database column names to be all lower case for sure
	@Column(name = "\"lastname\"")
	private String lastName = "";

	// BeanValidation instruction to validate all the list items
	@Valid
	@Size(min = 0, max = 12)
	// JAXB: Have these items as elements
	// @XmlElementWrapper(name = "phoneNumbers")
	// @XmlElement(name = "phoneNumber")
	// JAXB: If you want to include the phone numbers with the 1st time providing the json, uncomment the two
	// annotations above and comment the annotation below. Providing all the data will generate unnecessary overhead if
	// there are lots of persons to load, all the phone number content will be loaded as well when generating the json
	@XmlTransient
	// JPA: One person may have several phone numbers
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
	private List<PhoneNumber> phoneNumbers = null;

	/**
	 * Default constructor.
	 */
	public Person() {
		this.init(false);
	}

	public Person(boolean populateDefault) {
		this.init(populateDefault);
	}

	private void init(boolean populateDefault) {
		this.setNames(new ArrayList<String>());
		this.setPhoneNumbers(new ArrayList<PhoneNumber>());
		if (populateDefault) {
			this.addName("Name(s)");
			this.setLastName("LastName");
			PhoneNumber phoneNumber = new PhoneNumber(this, Type.CELL, "1234567890", "default");
			this.addNumber(phoneNumber);
		}
	}

	public int getId() {

		return id;
	}

	/**
	 * This is a convenience method for getting the first name.
	 * 
	 * @return The first name of the person
	 */
	public String getFirstName() {
		if (this.getNames().size() > 0) {
			return this.getNames().get(0);
		} else {
			return "No name";
		}
	}

	/**
	 * This is a convenience method for adding a name.
	 * 
	 * @return The first name of the person
	 */
	public void addName(String name) {
		this.getNames().add(name);
	}

	/**
	 * This is a convenience method for changing and adding a name. If the index is less than zero (&lt;0) or more than
	 * the max index of the array, the name will be added to the list.
	 * 
	 * @param index The index to have the name set to (note: index starts from '0')
	 * @param name The name to set
	 * @param push <code>True</code> not to replace existing name but to push the contents and insert the new name in
	 *        between
	 */
	public void modifyNamesList(int index, String name, boolean push) {
		if (this.getNames().size() >= index || index < 0) {
			// The name will be simply added as new
			this.getNames().add(name);
		} else {
			this.getNames().set(index, name);
		}
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public void setNames(String names) {

		this.names = new ArrayList<>();
		String[] namesList = names.split(" ");
		for (String name : namesList) {
			// Add the name only if it not empty String
			if (!StringUtils.isEmpty(name)) {
				// Remove empty spaces left & right
				String trimmedName = name.trim();
				this.addName(trimmedName);
			}
		}
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void addNumber(PhoneNumber phoneNumber) {
		this.getPhoneNumbers().add(phoneNumber);
	}

	public void removeNumber(PhoneNumber phoneNumber) {
		this.getPhoneNumbers().remove(phoneNumber);
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
}