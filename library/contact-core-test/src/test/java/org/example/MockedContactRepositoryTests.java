package org.example;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This leverages Mockito to act as the implementation to validate the that {@link ContactRepositoryTests}
 * are written correctly before they are used by any other actual implementations test cases.
 */
class MockedContactRepositoryTests extends ContactRepositoryTests {

	private List<Contact> savedContacts;

	@BeforeEach
	void init() {
		savedContacts = new ArrayList<>();
		contactRepository = Mockito.mock(ContactRepository.class);

		Mockito.when(contactRepository.findContact(Mockito.anyInt())).thenAnswer(invocationOnMock -> {
			Integer argument = invocationOnMock.getArgument(0);
			ContactRepositoryValidator.notNull(argument);
			Contact contact = null;
			if (argument != 0) {
				contact = new Contact(argument, RandomString.make(), RandomString.make());
			}
			return contact;
		});

		Mockito.when(contactRepository.findContacts(Mockito.any(), Mockito.any()))
				.thenAnswer(invocationOnMock -> {
					String firstName = invocationOnMock.getArgument(0);
					String lastName = invocationOnMock.getArgument(1);
					ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName);


					return savedContacts.stream()
							.filter(contact -> {
								if (firstName != null && lastName != null) {
									//only match on both if they are not null
									return contact.getFirstName().equals(firstName) &&
											contact.getLastName().equals(lastName);
								}
								return contact.getFirstName().equals(firstName)
										|| contact.getLastName().equals(lastName);
							})
							.collect(Collectors.toList());
				});

		Mockito.when(contactRepository.saveContact(Mockito.any())).thenAnswer(invocation -> {
			Contact contactToSave = invocation.getArgument(0);
			ContactRepositoryValidator.notNull(contactToSave);

			Contact savedContact = null;
			if (contactToSave.getId() == null) {
				Random random = new Random(System.currentTimeMillis());
				savedContact = new Contact(random.nextInt(), contactToSave.getFirstName(), contactToSave.getLastName());
				savedContacts.add(savedContact);
			} else {
				for (Contact updateContact : savedContacts) {
					if (updateContact.getId().equals(contactToSave.getId())) {
						updateContact.setFirstName(contactToSave.getFirstName());
						updateContact.setLastName(contactToSave.getLastName());
						savedContact = updateContact;
					}
				}
			}
			if (savedContact == null) {
				throw new IllegalArgumentException("Contact doesn't exist to update");
			}
			return savedContact;
		});
	}
}
