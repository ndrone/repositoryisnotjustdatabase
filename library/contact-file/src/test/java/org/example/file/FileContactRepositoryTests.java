package org.example.file;

import org.example.ContactException;
import org.example.ContactRepositoryTests;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

class FileContactRepositoryTests extends ContactRepositoryTests {

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
	void findByIdFileNotFound() {
		boolean deleted = file.delete();
		Assumptions.assumeTrue(deleted, () -> "File must be deleted for this test to be valid");
		ContactException contactException = Assertions.assertThrows(ContactException.class,
				() -> contactRepository.findContact(0));
		Assertions.assertNotNull(contactException);
	}

}
