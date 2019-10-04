package org.example.jdbc;

import org.example.Contact;
import org.example.ContactRepository;
import org.example.ContactRepositoryValidator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class JdbcContactRepository implements ContactRepository {

	private final ContactEntityRepository contactEntityRepository;

	JdbcContactRepository(ContactEntityRepository contactEntityRepository) {
		Assert.notNull(contactEntityRepository, "ContactEntityRepository must not be null");
		this.contactEntityRepository = contactEntityRepository;
	}

	@Override
	public Contact findContact(Integer id) {
		ContactRepositoryValidator.notNull(id);

		ContactEntity contactEntity = contactEntityRepository.findById(id).orElse(null);
		if (contactEntity == null) {
			return null;
		} else {
			return new Contact(contactEntity.getId(), contactEntity.getFirstName(), contactEntity.getLastName());
		}
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName) {
		ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName);

		Iterable<ContactEntity> contactEntities;
		if (firstName != null && lastName != null) {
			contactEntities = contactEntityRepository
					.findAllByFirstNameAndLastName(firstName, lastName);
		} else {
			contactEntities = contactEntityRepository.findAllByFirstNameOrLastName(firstName, lastName);
		}
		List<Contact> contacts = new ArrayList<>();
		contactEntities.forEach(contactEntity -> contacts.add(
				new Contact(contactEntity.getId(), contactEntity.getFirstName(), contactEntity.getLastName())));
		return contacts;
	}

	@Override
	public Contact saveContact(Contact contact) {
		ContactRepositoryValidator.notNull(contact);

		ContactEntity contactEntity;
		if (contact.getId() == null) {
			contactEntity =
					new ContactEntity(contact.getId(), contact.getFirstName(), contact.getLastName());

		} else {
			if (Optional.ofNullable(findContact(contact.getId())).isPresent()) {
				// update contact
				contactEntity = new ContactEntity(contact.getId(), contact.getFirstName(), contact.getLastName());
			} else {
				throw new IllegalArgumentException(
						String.format("Contact with id %s not found can't update", contact.getId()));
			}
		}
		contactEntity = contactEntityRepository.save(contactEntity);
		return new Contact(contactEntity.getId(), contactEntity.getFirstName(), contactEntity.getLastName());
	}
}
