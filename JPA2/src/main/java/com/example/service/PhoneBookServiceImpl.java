package com.example.service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Default;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
import com.example.entity.PhoneNumber_;
import com.example.service.interfaces.PhoneBookService;

// CDI: Differentiate between different objects of the same type bound in the same scope
@Named
// CDI: Will be used by 'default' when injecting implementation of some interface
@Default
// JaveEE7: Provides the application the ability to declaratively control transaction boundaries on CDI managed beans
@Transactional
public class PhoneBookServiceImpl implements Serializable, PhoneBookService {

	private static final long serialVersionUID = 1L;
	@PersistenceContext(unitName = "j4aPU")
	private EntityManager em;

	public PhoneBookServiceImpl() {
	}

	@PostConstruct
	public void init() {
	}

	@Override
	// No transaction is needed with simply getting a person, but one can be around if this is involved with some other
	// Transaction
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Person getPerson(int id) {

		Person person = this.em.find(Person.class, id);
		// force the entity to be up to date
		if (person != null) {
			this.em.refresh(person);
		}
		return person;
	}

	@Override
	// No transaction is needed when simply getting (loading) a person
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Person> getAllPersons() {

		Query query = this.em.createNamedQuery(Person.FIND_ALL, Person.class);
		// Note: Even that we know the result type will be ok, this will generate a warning
		// To avoid these warnings you could use the Criteria API (example in getPersonsWithPhoneNumberLike())
		List<Person> persons = query.getResultList();
		return persons;
	}

	@Override
	// No transaction is needed when simply getting (loading) a person
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Person> getPersonsByLastName(String lastname) {

		// This uses JPQL with parameters
		Query query = this.em.createNamedQuery(Person.FIND_BY_LASTNAME, Person.class);
		query.setParameter(Person.PARAM_LASTNAME, lastname);
		List<Person> persons = query.getResultList();
		return persons;
	}

	@Override
	// Will create a new transaction for each persist request
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void addPerson(Person person) {
		try {
			this.em.persist(person);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to persist: " + e.getMessage());
		}
	}

	@Override
	// Will create a new transaction for each update request if none exists yet
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updatePerson(Person person) {
		// Merging the entity with the database will have the data synchronized (including the phone numbers)
		this.em.merge(person);
		// Flush the changes into db
		this.em.flush();
	}

	@Override
	// Will create a new transaction which will be used with all update calls
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updatePersons(List<Person> persons) {
		// The same transaction is used for all updates. If one fails, then all fail => rollback
		for (Person person : persons) {
			this.updatePerson(person);
		}
	}

	@Override
	// Will create a new transaction for each remove request
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deletePerson(Person person) {
		this.em.remove(person);
	}

	@Override
	public void deletePerson(int personId) {
		// In JPA, before deleting a person you must first get a reference to the entity
		// unless using JPQL or Criteria API
		Person person = this.getPerson(personId);
		this.deletePerson(person);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addPhoneNumber(PhoneNumber phoneNumber) {
		this.em.persist(phoneNumber);
		// this.updatePerson(phoneNumber.getOwner());
	}

	@Override
	public void deletePhoneNumber(PhoneNumber phoneNumber) {
		this.em.remove(phoneNumber);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deletePhoneNumber(int phoneNumberId) {
		PhoneNumber pn = this.em.find(PhoneNumber.class, phoneNumberId);
		this.deletePhoneNumber(pn);
	}

	@Override
	public List<Person> getPersonsWithPhoneNumberLike(String number, SearchType searchType) {

		// The meta model generation is introduces in pom.xml
		// You need to compile the project once to have generated files (compile with maven).
		// (Eclipse) Also have the generated sources folder configured in project build path!
		// Right click on "project name" > 'properties' > 'Java Build Path'
		// Select 'Add Folder' and choose 'target' > 'generated-sources'

		// sql: <select> <from> <where>

		// Query with Criteria API
		// Create the criteria builder
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		// Define the type of the objects the query will return in the end
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		// Criteria queries may have more than one query root. This usually occurs when the query navigates from several
		// entities
		// The FROM part
		Root<Person> proot = cq.from(Person.class);
		Root<PhoneNumber> pnroot = cq.from(PhoneNumber.class);
		// Define the join by telling where the join will happen
		// The Persons will be collected from the list that the phone numbers have as 'owner's
		// The correct oder to join is to have persons to be joined with phone number since the criteria is targeted on
		// phone number
		Join<PhoneNumber, Person> owners = pnroot.join(PhoneNumber_.owner);
		// The actual CRITERIA for the WHERE
		Predicate criteria = this.createPhoneNumberCriteria(cb, pnroot, number, searchType);
		// The SELECT part
		// since we do the actual query by limiting the phone numbers to get the persons from, we use the object of the
		// 'Join' type
		cq.select(owners);
		// Since it is possible that several phone numbers have the same owner and we are only interested to have one of
		// each, we define the results to be distinct => select DISTINCT Person p from ...
		cq.distinct(true);
		// The WHERE part (consists of CRITERIA)
		// There may be multiple criteria => use and, or, ...
		// cq.where(cb.and(criteria1, criteria2));
		cq.where(criteria);
		// Finally create the typed query from the criteria query object
		TypedQuery<Person> query = this.em.createQuery(cq);
		// Use the typed query to get the results (remember, the type of outcome was defined in the start already)
		List<Person> matchingPersons = query.getResultList();
		return matchingPersons;
	}

	@Override
	public List<PhoneNumber> getAllPhoneNumbers() {

		Query query = this.em.createNamedQuery(PhoneNumber.FIND_ALL, PhoneNumber.class);
		List<PhoneNumber> numbers = query.getResultList();
		return numbers;
	}

	@Override
	public List<PhoneNumber> getPhoneNumbersLike(String number, SearchType searchType) {

		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<PhoneNumber> cq = cb.createQuery(PhoneNumber.class);
		Root<PhoneNumber> pnroot = cq.from(PhoneNumber.class);
		Predicate criteria = this.createPhoneNumberCriteria(cb, pnroot, number, searchType);
		cq.select(pnroot);
		cq.where(criteria);
		TypedQuery<PhoneNumber> query = this.em.createQuery(cq);
		List<PhoneNumber> matchingNumbers = query.getResultList();
		return matchingNumbers;
	}

	/**
	 * Creates a criteria to limit query results.
	 * 
	 * @param cb
	 * @param pnroot
	 * @param number
	 * @param searchType
	 * @return
	 */
	private Predicate createPhoneNumberCriteria(CriteriaBuilder cb, Root<PhoneNumber> pnroot, String number,
	        SearchType searchType) {

		Path<String> path = pnroot.get(PhoneNumber_.phoneNumber);
		Predicate criteria = null;
		switch (searchType) {
		case STARTS:
			criteria = cb.like(path, number + "%");
			break;
		case ENDS:
			criteria = cb.like(path, "%" + number);
			break;
		case EQUALS:
			criteria = cb.equal(path, number);
			break;
		default:
			criteria = cb.like(path, "%" + number + "%");
			break;
		}
		return criteria;
	}
}