package com.example.service.interfaces;

import java.util.List;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;

/**
 * Interface to specify basic CRUD (Create, Read, Update, Delete) operations for person data handling.
 */
public interface PhoneBookService {

	public enum SearchType {
		STARTS, ENDS, LIKE, EQUALS
	}

	/**
	 * Get single person.
	 * 
	 * @param id The id of the person to get
	 * @return A person from the db
	 */
	public Person getPerson(int id);

	/**
	 * Gets all persons.
	 * 
	 * @return A list of all persons in the db
	 */
	public List<Person> getAllPersons();
	
	/**
	 * Gets all phone numbers.
	 * 
	 * @return A list of all phone numbers in the db
	 */
	public List<PhoneNumber> getAllPhoneNumbers();

	/**
	 * Gets persons by last name.
	 * 
	 * @param lastname The last name to get persons by
	 * @return A list of all persons by the last name in the db
	 */
	public List<Person> getPersonsByLastName(String lastname);

	/**
	 * Adds a person.
	 * 
	 * @param person The person to add (note the id is generated when the person is persisted)
	 */
	public void addPerson(Person person);

	/**
	 * Removes a person.
	 * 
	 * @param person The person to remove.
	 */
	public void deletePerson(Person person);

	/**
	 * Removes a person by id.
	 * 
	 * @param personId The if of the person to remove.
	 */
	public void deletePerson(int personId);

	/**
	 * Updates person data.
	 * 
	 * @param person The person data to update.
	 */
	public void updatePerson(Person person);

	/**
	 * Will update the persons in the provided list.
	 * 
	 * @param persons The list of persons to update
	 */
	public void updatePersons(List<Person> persons);

	/**
	 * Add a number for a person (must have the owner).
	 * 
	 * @param phoneNumber The person data to update.
	 */
	public void addPhoneNumber(PhoneNumber phoneNumber);

	/**
	 * Removes the phone number from the database.
	 * 
	 * @param phoneNumber The phone number to remove
	 */
	public void deletePhoneNumber(PhoneNumber phoneNumber);

	/**
	 * Removes the specified phone number from the database.
	 * 
	 * @param phoneNumberId The id of the phone number to remove
	 */
	public void deletePhoneNumber(int phoneNumberId);
	
	/**
	 * Does a search on all phone numbers in the database, finding numbers that have a phone number similar to provided one.
	 * 
	 * @param number The String of number (can be text) to look similar numbers for
	 * @param searchType If the phone number is expected to start or end with the given number/text, or to be a
	 *        "middle part" of the number
	 * @return A list of phone numbers having the similar number
	 */
	public List<PhoneNumber> getPhoneNumbersLike(String number, SearchType searchType);
	
	/**
	 * A convenience method for getPersonsWithPhoneNumberLike(String number, SearchType searchType).
	 * 
	 * @param number The String of number (can be text) to look similar numbers for
	 * @param searchType If the phone number is expected to start or end with the given number/text, or to be a
	 *        "middle part" of the number
	 * @return A list of persons having the similar number
	 * @see #getPhoneNumbersLike(String, SearchType)
	 */
	public List<Person> getPersonsWithPhoneNumbersLike(String number, String searchType);
	
	/**
	 * Does a search on all persons in the database, finding persons that have a phone number similar to provided one.
	 * 
	 * @param number The String of number (can be text) to look similar numbers for
	 * @param searchType If the phone number is expected to start or end with the given number/text, or to be a
	 *        "middle part" of the number
	 * @return A list of persons having the similar number
	 */
	public List<Person> getPersonsWithPhoneNumbersLike(String number, SearchType searchType);
}
