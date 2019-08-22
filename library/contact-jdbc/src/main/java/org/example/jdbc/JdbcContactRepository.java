package org.example.jdbc;

import org.example.Contact;
import org.example.ContactRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

class JdbcContactRepository implements ContactRepository {

	private final ContactEntityRepository contactEntityRepository;

	JdbcContactRepository(ContactEntityRepository contactEntityRepository) {
		Assert.notNull(contactEntityRepository, "ContactEntityRepository must not be null");
		this.contactEntityRepository = contactEntityRepository;
	}

	@Override
	public Contact saveContact(Contact contact) {
		Assert.isTrue(StringUtils.hasText(contact.getFirstName()) || StringUtils.hasText(contact.getLastName()),
				"Contact must have a first name or last name");

		ContactEntity contactEntity = new ContactEntity(contact.getId(), contact.getFirstName(), contact.getLastName());
		contactEntity = contactEntityRepository.save(contactEntity);
		return new Contact(contactEntity.getId(), contactEntity.getFirstName(), contactEntity.getLastName());
	}

	@Override
	public Contact findContact(Integer id) {
		Assert.notNull(id, "Contact id must have a value");

		ContactEntity contactEntity = contactEntityRepository.findById(id).orElse(new ContactEntity());
		return new Contact(contactEntity.getId(), contactEntity.getFirstName(), contactEntity.getLastName());
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName) {
		Assert.isTrue(StringUtils.hasText(firstName) || StringUtils.hasText(lastName),
				"Fist name or last name must have a value");

		List<Contact> contacts = new ArrayList<>();
		Iterable<ContactEntity> contactEntities = contactEntityRepository
				.findAllByFirstNameAndLastName(firstName, lastName);
		contactEntities.forEach(contactEntity ->
				contacts.add(new Contact(contactEntity.getId(), contactEntity.getFirstName(), contactEntity.getLastName())));
		return contacts;
	}
}
