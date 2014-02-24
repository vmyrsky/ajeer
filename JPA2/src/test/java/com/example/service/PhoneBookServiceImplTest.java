package com.example.service;

import java.util.ArrayList;
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
		System.out.println("Did you remember to start DB?");
	}

	@After
	public void cleanup() {
		// Implement cleanup if necessary
	}

	@Test
	public void dummy() {
		assertTrue(true);
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
		phoneNumber.setOwner(person);
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
			phoneNumber.setOwner(person);
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
}