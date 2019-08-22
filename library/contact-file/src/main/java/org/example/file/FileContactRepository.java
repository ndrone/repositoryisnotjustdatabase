package org.example.file;


import org.example.Contact;
import org.example.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

class FileContactRepository implements ContactRepository {

	private static final Logger log = LoggerFactory.getLogger(FileContactRepository.class);

	private File contactFile;

	public FileContactRepository(String absoluteFilePath) throws IOException {
		if (absoluteFilePath == null || absoluteFilePath.isEmpty()) {
			throw new IllegalArgumentException("absoluteFilePath must not be empty");
		}

		contactFile = new File(absoluteFilePath);
		if (!contactFile.exists()) {
			log.warn("{} doesn't exist... creating it", absoluteFilePath);
			contactFile.createNewFile();
			log.info("Created file {}", absoluteFilePath);
		}
	}

	@Override
	public Contact saveContact(Contact contact) {
		return null;
	}

	@Override
	public Contact findContact(Integer id) {
		return null;
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName) {
		return null;
	}
}
