package com.example.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.entity.Person;
import com.example.entity.PhoneNumber;
import com.example.service.interfaces.PhoneBookService.SearchType;

/**
 * A simple test to check the JPA can interact with the database. For this you need a derbyDB be running in your system.
 * A 'phonebooktest' db folder is created in your home directory. Delete this folder manually if the test reports
 * problems when creating the database.
 * 
 * An issue with Jersey and Arquillian was discovered when these were in the same package, preventing from running
 * arquillian test. Therefore you have to keep this stuff in separate jar files. (It would be smart anyway, but this
 * issue kind of forces into it).
 */
@RunWith(Arquillian.class)
public class PhoneBookServiceImplTest extends TestCase {

	/**
	 * The resource injected that should be tested
	 */
	@Inject
	private PhoneBookServiceImpl service;
	/**
	 * You need to start and stop the transaction manually in tests when one is required.
	 */
	@Resource
	private UserTransaction utx;

	/**
	 * The EntityManager and all entities used must be included (entities introduced in package). Also all the resources
	 * needed to be injected must be included here.<br/>
	 * <b>NOTE</b>: The name of the test resources must not be the same as in production, or they will override the
	 * values specified in them when developing (if the test-folder happens to be included). <br/>
	 * - <code>test.jar</code>: It must be in some sort of virtual archive (e.g. could be web also)<br/>
	 * - <code>beans.xml</code>: You must provide an empty beans.xml to enable CDI<br/>
	 * - <code>persistence.xml</code>: A persistence.xml file is needed for the JPA (there must also be arquillian.xml,
	 * but it is not defined here)<br/>
	 * - <code>*.class</code>: All classes needed to be injected must be introduced.<br/>
	 * &nbsp;* Can be done by:<br/>
	 * &nbsp;&nbsp;- <code>addClass</code>: Add a single class at a time<br/>
	 * &nbsp;&nbsp;- <code>addClasses</code>: Add multiple classes<br/>
	 * &nbsp;&nbsp;- <code>addPackage</code>: Add all classes in a package<br/>
	 */
	@Deployment
	public static JavaArchive createDeployment() {

		JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
		        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
		        .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
		jar.addClass(PhoneBookServiceImpl.class);
		jar.addClasses(UserTransaction.class, EntityManager.class);
		jar.addPackage(Person.class.getPackage());

		String contents = jar.toString(true);
		// Check this output if you have 'WELD-001408' errors => add missing dependencies
		System.out.println("JAR contents: " + contents);

		return jar;
	}

	@BeforeClass
	public static void init() {
		System.out.println("Did you remember to start DB or shut down the container (e.g. GlassFish)?");
		// Note: Noticed that if you use EJBContainer to create tests, arquillian will fail as it will gulp resources
		// that also arquillian needs (and I failed to figure out how to free them again)
	}

	@After
	public void cleanup() {
		try {
			this.utx.begin();
			Iterator<Person> iter = this.service.getAllPersons().iterator();
			while (iter.hasNext()) {
				this.service.deletePerson(iter.next());
			}
			this.utx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testInjection() {
		assertNotNull(this.service);
	}

	@Test
	public void testPersisting() {

		assertEquals(0, this.service.getAllPersons().size());
		Person person = new Person();
		person.setLastName("Tester");
		List<String> names = new ArrayList<>();
		names.add("John");
		names.add("william");
		person.setNames(names);
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setDescription("Main number");
		phoneNumber.setNumberType(PhoneNumber.Type.CELL);
		phoneNumber.setPhoneNumber("+35840-7897890");
		phoneNumber.setDescription("Test description 1");
		person.addNumber(phoneNumber);
		try {
			// You need to begin and end the transactions in tests
			// BeanValidator bv = new BeanValidator();
			// List<ConstraintViolationMessage> messages = bv.validate(person);
			// System.out.print(messages.toString());
			this.utx.begin();
			this.service.addPerson(person);
			this.utx.commit();
			// Add a second number
			phoneNumber = new PhoneNumber();
			phoneNumber.setDescription("Work number");
			phoneNumber.setNumberType(PhoneNumber.Type.WORK);
			phoneNumber.setPhoneNumber("+3589-4564564");
			phoneNumber.setDescription("Test description 2");
			person.addNumber(phoneNumber);
			this.utx.begin();
			this.service.updatePerson(person);
			this.utx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(1, this.service.getAllPersons().size());
		Person persistedPerson = null;
		try {
			// Transaction not needed for simple load operations generally, but since there is annotation to use one, it
			// is needed
			this.utx.begin();
			persistedPerson = this.service.getPerson(person.getId());
			this.utx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(persistedPerson);
		assertEquals("Tester", persistedPerson.getLastName());
		assertEquals(2, persistedPerson.getPhoneNumbers().size());
		assertEquals("+35840-7897890", persistedPerson.getPhoneNumbers().get(0).getPhoneNumber());
		assertEquals(PhoneNumber.Type.CELL, persistedPerson.getPhoneNumbers().get(0).getNumberType());
		assertEquals("Test description 1", persistedPerson.getPhoneNumbers().get(0).getDescription());
		assertEquals("+3589-4564564", persistedPerson.getPhoneNumbers().get(1).getPhoneNumber());
		assertEquals(PhoneNumber.Type.WORK, persistedPerson.getPhoneNumbers().get(1).getNumberType());
		assertEquals("Test description 2", persistedPerson.getPhoneNumbers().get(1).getDescription());
	}

	@Test
	public void testGettingPersonsListByNumber() {
		Person person1 = new Person(true);
		person1.setLastName("Tester1");
		PhoneNumber phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("111-11111111");
		person1.addNumber(phoneNumber);
		phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("222-11111111");
		person1.addNumber(phoneNumber);

		Person person2 = new Person(true);
		person2.setLastName("Tester2");
		phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("111-2222222");
		person2.addNumber(phoneNumber);
		phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("333-2222222");
		person2.addNumber(phoneNumber);

		Person person3 = new Person(true);
		person3.setLastName("Tester3");
		phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("111-33333333");
		person3.addNumber(phoneNumber);
		phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("444-33333333");
		person3.addNumber(phoneNumber);
		person3.addNumber(phoneNumber);
		phoneNumber = new PhoneNumber(true);
		phoneNumber.setPhoneNumber("555-11111111");
		person3.addNumber(phoneNumber);

		try {
			this.utx.begin();
			this.service.addPerson(person1);
			this.service.addPerson(person2);
			this.service.addPerson(person3);
			this.utx.commit();
			List<PhoneNumber> numbers = this.service.getAllPhoneNumbers();
			// Each person was assigned a 'default number'
			assertEquals(10, numbers.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		String equal = "555-11111111";
		List<PhoneNumber> one = this.service.getPhoneNumbersLike(equal, SearchType.EQUALS);
		assertEquals(1, one.size());

		String like = "3333";
		List<PhoneNumber> numbers = this.service.getPhoneNumbersLike(like, SearchType.LIKE);
		assertEquals(2, numbers.size());

		String startsWith = "111";
		numbers = this.service.getPhoneNumbersLike(startsWith, SearchType.STARTS);
		assertEquals(3, numbers.size());

		String endsWith = "222";
		numbers = this.service.getPhoneNumbersLike(endsWith, SearchType.ENDS);
		assertEquals(2, numbers.size());

		List<Person> persons = this.service.getPersonsWithPhoneNumberLike(like, SearchType.LIKE);
		assertEquals(1, persons.size());

		startsWith = "111";
		persons = this.service.getPersonsWithPhoneNumberLike(startsWith, SearchType.STARTS);
		assertEquals(3, persons.size());

		endsWith = "111";
		persons = this.service.getPersonsWithPhoneNumberLike(endsWith, SearchType.ENDS);
		assertEquals(2, persons.size());
	}
}