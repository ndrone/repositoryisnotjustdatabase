package org.example.http;

import org.example.Contact;
import org.example.ContactException;
import org.example.ContactRepository;
import org.example.ContactRepositoryValidator;

class HttpContactRepository implements ContactRepository {
	@Override
	public Contact findContact(Integer id) throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.notNull(id);
		return null;
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName) throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName);
		return null;
	}

	@Override
	public Contact saveContact(Contact contact) throws IllegalArgumentException, ContactException {
		ContactRepositoryValidator.notNull(contact);
		return null;
	}
}
