package org.example;

import java.util.Objects;
import java.util.stream.Stream;

public final class ContactRepositoryValidator {

	private ContactRepositoryValidator() {
	}

	public static void notNull(Integer id) {
		if (null == id) {
			throw new IllegalArgumentException("Id to search on must not be null");
		}
	}

	public static void notNull(Contact contact) {
		if (null == contact) {
			throw new IllegalArgumentException("Contact must not be null");
		}
		firstNameOrLastNameHasText(contact.getFirstName(), contact.getLastName());
	}

	public static void firstNameOrLastNameHasText(String firstName, String lastName) {
		if (Stream.of(firstName, lastName)
				.noneMatch(arg -> Objects.nonNull(arg) && !arg.isEmpty() && !arg.isBlank())) {
			throw new IllegalArgumentException("Neither firstName or lastName have text");
		}
	}
}
