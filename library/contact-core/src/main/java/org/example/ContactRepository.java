package org.example;

public interface ContactRepository {

	Contact saveContact(Contact contact);

	Contact findContact(Integer id);

	Iterable<Contact> findContacts(String firstName, String lastName);
}
