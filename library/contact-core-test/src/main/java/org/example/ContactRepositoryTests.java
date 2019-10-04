package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * This class is used to test and verify that any implementations of the interface
 * are implemented correctly.
 */
public class ContactRepositoryTests {

	protected ContactRepository contactRepository;

	@Test
	void findByIdNotFound() {
		Contact contact = contactRepository.findContact(0);
		Assertions.assertNull(contact, "per interface if contact is not found should return null");
	}

	@Test
	void saveNullContact() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.saveContact(null));
		Assertions.assertNotNull(exception, "can't save null contact");

		exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.saveContact(new Contact()));
		Assertions.assertNotNull(exception, "fields of contact must have text");
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
		Assertions.assertEquals(contact.getId().intValue(), saved.getId().intValue(),
				"just updating contact id should be the same");
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

	private static Stream<Arguments> provideStringsForIsBlank() {
		return Stream.of(
				Arguments.of(null, null),
				Arguments.of("", ""),
				Arguments.of("  ", " ")
		);
	}

	@ParameterizedTest
	@MethodSource("provideStringsForIsBlank")
	void findByNamesAreEmptyOrBlank(String firstName, String lastName) {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> contactRepository.findContacts(firstName, lastName));
		Assertions.assertNotNull(exception, "firstName or lastName must have text");
	}

	@Test
	void findByFirstNameAndLastName() {
		generateContacts();
		String firstName = "john";
		String lastName = "doe";
		Iterable<Contact> contacts = contactRepository.findContacts(firstName, lastName);
		for (Contact contact : contacts) {
			Assertions.assertEquals(firstName, contact.getFirstName(), "first name must match");
			Assertions.assertEquals(lastName, contact.getLastName(), "last name must match");
		}
	}

	@Test
	void findByFirstName() {
		generateContacts();
		String firstName = "john";
		Iterable<Contact> contacts = contactRepository.findContacts(firstName, null);
		for (Contact contact : contacts) {
			Assertions.assertEquals(firstName, contact.getFirstName(), "first names must match");
		}
	}

	@Test
	void findByLastName() {
		generateContacts();
		String lastName = "doe";
		Iterable<Contact> contacts = contactRepository.findContacts(null, lastName);
		for (Contact contact : contacts) {
			Assertions.assertEquals(lastName, contact.getLastName(), "last names must match");
		}
	}

	@Test
	void findByNames() {
		generateContacts();
		String firstName = "blah";
		Iterable<Contact> contacts = contactRepository.findContacts(firstName, null);
		Assertions.assertFalse(contacts.iterator().hasNext(), "no contacts should be found");
	}

	private void generateContacts() {
		save(new Contact(null, "john", "doe"));
		save(new Contact(null, "john", "test"));
		save(new Contact(null, "jane", "doe"));
	}

}
