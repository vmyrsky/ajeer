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
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
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

		Query query = this.em.createNamedQuery(Person.FIND_ALL);
		List<Person> persons = query.getResultList();
		return persons;
	}

	@Override
	// No transaction is needed when simply getting (loading) a person
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Person> getPersonsByLastName(String lastname) {

		Query query = this.em.createNamedQuery(Person.FIND_BY_LASTNAME);
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
	public List<Person> searchWithPhoneNumberLike(String number, SearchType searchType) {

		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		// Criteria queries may have more than one query root. This usually occurs when the query navigates from several
		// entities
		Root<Person> proot = cq.from(Person.class);
		Root<PhoneNumber> pnroot = cq.from(PhoneNumber.class);
		// The meta model generation is introduces in pom.xml.
		// You need to compile the project once to have generated files (compile with maven).
		// (Eclipse) Also have the generated sources folder configured in project build path!
		// Right click on "project name" > 'properties' > 'Java Build Path'
		// Select 'Add Folder' and choose 'target' > 'generated-sources'

		// EntityType<PhoneNumber> PhoneNumber_ = pnroot.getModel();
		// Join<PhoneNumber, Person> owner = cq.join(PhoneNumber_.owner).join(Person_.names);
		cq.select(proot);
		TypedQuery<Person> query = em.createQuery(cq);
		List<Person> matchingPersons = query.getResultList();
		return matchingPersons;
	}
}