package org.example.file;

import org.example.Contact;
import org.example.ContactException;
import org.example.ContactRepository;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

class FileContactRepositoryTests {

	private ContactRepository contactRepository;
	private File file;

	@BeforeEach
	void init() throws IOException {
		file = File.createTempFile("output", ".txt");
		contactRepository = new FileContactRepository(file.getAbsolutePath());
	}

	@AfterEach
	void tearDown() {
		file.delete();
	}

	@Test
	void constructorException() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
				new FileContactRepository(null));
		Assertions.assertNotNull(exception);
	}

	@Test
	void existingFile() {
		Assertions.assertNotNull(contactRepository);
	}

	@Test
	void createFile() throws IOException {
		File file = File.createTempFile("output", ".txt");
		String absolutePath = file.getAbsolutePath();
		String tempFilePath = absolutePath.
				substring(0, absolutePath.lastIndexOf(File.separator));

		contactRepository = new FileContactRepository(tempFilePath + File.separator + "output.txt");
		Assertions.assertNotNull(contactRepository);
		file.delete();
	}

	@Test
	void findWithNullIdThrowsException() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.findContact(null));
		Assertions.assertNotNull(exception);
	}

	@Test
	void findByIdNotFound() {
		Contact contact = contactRepository.findContact(0);
		Assertions.assertNull(contact);
	}

	@Test
	void findByIdFileNotFound() {
		boolean deleted = file.delete();
		Assumptions.assumeTrue(deleted, () -> "File must be deleted for this test to be valid");
		ContactException contactException = Assertions.assertThrows(ContactException.class,
				() -> contactRepository.findContact(0));
		Assertions.assertNotNull(contactException);
	}

	@Test
	void saveNullContact() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.saveContact(null));
		Assertions.assertNotNull(exception);

		exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.saveContact(new Contact()));
		Assertions.assertNotNull(exception);
	}

	@Test
	void saveContact() {
		Contact contact = save(new Contact(null, "test", "test"));
		Assertions.assertNotNull(contact.getId(), "saved contact must have an id");
	}

	@Test
	void updateContact() {
		Contact contact = save(new Contact(null, "test", "test"));
		contact.setLastName("contact");

		Contact saved = save(contact);
		Assertions.assertEquals(contact.getId(), saved.getId(), "just updating contact id should be the same");
		Assertions.assertEquals("contact", saved.getLastName(), "validating last name changed.");
	}

	@Test
	void savedContactUpdateNotFound() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.saveContact(new Contact(1, "test", "test")));
		Assertions.assertNotNull(exception, "Trying to update a contact that doesn't already exist.");
	}

	private Contact save(Contact contact) {
		return contactRepository.saveContact(contact);
	}

	@Test
	void findByNamesNull() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.findContacts(null, null));
		Assertions.assertNotNull(exception);
	}

	@Test
	void findByFirstNameAndLastName() {
		generateContacts();
		String firstName = "john";
		String lastName = "doe";
		Iterable<Contact> contacts = contactRepository.findContacts(firstName, lastName);
		for (Contact contact : contacts) {
			Assertions.assertEquals(firstName, contact.getFirstName());
			Assertions.assertEquals(lastName, contact.getLastName());
		}
	}

	@Test
	void findByFirstName() {
		generateContacts();
		String firstName = "john";
		Iterable<Contact> contacts = contactRepository.findContacts(firstName, null);
		for (Contact contact : contacts) {
			Assertions.assertEquals(firstName, contact.getFirstName());
		}
	}

	@Test
	void findByLastName() {
		generateContacts();
		String lastName = "doe";
		Iterable<Contact> contacts = contactRepository.findContacts(null, lastName);
		for (Contact contact : contacts) {
			Assertions.assertEquals(lastName, contact.getLastName());
		}
	}

	@Test
	void findByNames() {
		generateContacts();
		String firstName = "blah";
		Iterable<Contact> contacts = contactRepository.findContacts(firstName, null);
		Assertions.assertFalse(contacts.iterator().hasNext());
	}

	private void generateContacts() {
		save(new Contact(null, "john", "doe"));
		save(new Contact(null, "john", "test"));
		save(new Contact(null, "jane", "doe"));
	}

}
