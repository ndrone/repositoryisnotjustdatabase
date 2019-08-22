package org.example.file;


import com.opencsv.*;
import org.example.Contact;
import org.example.ContactException;
import org.example.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class FileContactRepository implements ContactRepository {

	private static final Logger log = LoggerFactory.getLogger(FileContactRepository.class);

	private static final int ID_POSITION = 0;
	private static final int FIRST_NAME_POSITION = 1;
	private static final int LAST_NAME_POSITION = 2;
	private static final char DELIMITER = ',';

	private final File contactFile;
	private final CSVParser parser;

	FileContactRepository(String absoluteFilePath) throws IOException {
		if (absoluteFilePath == null || absoluteFilePath.isEmpty()) {
			throw new IllegalArgumentException("absoluteFilePath must not be empty");
		}

		contactFile = new File(absoluteFilePath);
		if (!contactFile.exists()) {
			log.warn("{} doesn't exist... creating it", absoluteFilePath);
			contactFile.createNewFile();
			log.info("Created file {}", absoluteFilePath);
		}

		parser = new CSVParserBuilder().withSeparator(DELIMITER).withIgnoreQuotations(true).build();
	}

	@Override
	public Contact findContact(Integer id) {
		if (null == id) {
			throw new IllegalArgumentException("Id to search on must not be null");
		}

		String stringId = String.valueOf(id);
		String[] parts = readFile().stream().filter(line -> stringId.equals(line[ID_POSITION]))
				.findFirst().orElse(null);

		if (null == parts) {
			return null;
		} else {
			return new Contact(Integer.valueOf(parts[ID_POSITION]), parts[FIRST_NAME_POSITION], parts[LAST_NAME_POSITION]);
		}
	}

	private List<String[]> readFile() {
		try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(contactFile))
				.withSkipLines(0).withCSVParser(parser).build()) {
			return csvReader.readAll();
		} catch (FileNotFoundException e) {
			throw new ContactException(String.format("Contact file %s not found", contactFile.getAbsoluteFile()), e);
		} catch (IOException e) {
			throw new ContactException("Error while accessing/processing contact file", e);
		}
	}

	@Override
	public Iterable<Contact> findContacts(String firstName, String lastName) {
		if (null == firstName && null == lastName) {
			throw new IllegalArgumentException("firstName and lastName to search on must not be null");
		}
		Predicate<String[]> predicate;
		if (null == lastName) {
			predicate = line -> firstName.equals(line[FIRST_NAME_POSITION]);
		} else if (null == firstName) {
			predicate = line -> lastName.equals(line[LAST_NAME_POSITION]);
		} else {
			predicate = line -> firstName.equals(line[FIRST_NAME_POSITION]) && lastName.equals(line[LAST_NAME_POSITION]);
		}

		return readFile().stream().filter(predicate)
				.map(parts -> new Contact(Integer.valueOf(parts[ID_POSITION]), parts[FIRST_NAME_POSITION], parts[LAST_NAME_POSITION]))
				.collect(Collectors.toList());
	}

	@Override
	public Contact saveContact(Contact contact) {
		Contact saved = null;
		if (null == contact) {
			throw new IllegalArgumentException("Contact must not be null");
		} else if (null == contact.getFirstName() && null == contact.getLastName()) {
			throw new IllegalArgumentException("Contact firstName and lastName must not be null");
		} else if (null == contact.getId()) {
			// find next id and save
			String[] maxContactParts = readFile().stream()
					.max(Comparator.comparing(line -> Integer.valueOf(line[ID_POSITION]))).orElse(new String[]{"0"});
			saved = contact;
			saved.setId(Integer.parseInt(maxContactParts[ID_POSITION]) + 1);
			write(saved);
		} else {
			if (Optional.ofNullable(findContact(contact.getId())).isPresent()) {
				// update contact
				saved = contact;
				write(saved);
			} else {
				throw new IllegalArgumentException(
						String.format("Contact with id %s not found can't update", contact.getId()));
			}
		}
		return saved;
	}

	private void write(Contact contact) {
		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(contactFile))) {
			csvWriter.writeNext(
					new String[]{String.valueOf(contact.getId()), contact.getFirstName(), contact.getLastName()});
		} catch (IOException e) {
			throw new ContactException("Error while accessing/processing contact file", e);
		}
	}
}
