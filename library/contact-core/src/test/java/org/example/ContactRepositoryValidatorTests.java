package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ContactRepositoryValidatorTests {

	@Test
	void notNullIdException() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> ContactRepositoryValidator.notNull((Integer) null));
		Assertions.assertNotNull(exception);
	}

	@Test
	void notNullId() {
		Assertions.assertDoesNotThrow(() -> ContactRepositoryValidator.notNull(1));
	}

	@Test
	void notNullContactException() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> ContactRepositoryValidator.notNull((Contact) null));
		Assertions.assertNotNull(exception);
	}

	@Test
	void firstNameAndLastNameNull() {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> ContactRepositoryValidator.firstNameOrLastNameHasText(null, null));
		Assertions.assertNotNull(exception);
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
	void firstNameAndLastName(String firstName, String lastName) {
		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
				() -> ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName));
		Assertions.assertNotNull(exception);
	}

	@ParameterizedTest
	@MethodSource("provideFirstNameLastName")
	void firstNameOrLastName(String firstName, String lastName) {
		Assertions.assertDoesNotThrow(() -> ContactRepositoryValidator.firstNameOrLastNameHasText(firstName, lastName));
	}

	private static Stream<Arguments> provideFirstNameLastName() {
		return Stream.of(
				Arguments.of(null, "lastName"),
				Arguments.of("", "lastName"),
				Arguments.of(" ", "lastName"),
				Arguments.of("firstName", null),
				Arguments.of("firstName", ""),
				Arguments.of("firstName", " ")
		);
	}
}
